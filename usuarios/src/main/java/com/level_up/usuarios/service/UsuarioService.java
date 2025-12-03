package com.level_up.usuarios.service;

import com.level_up.usuarios.client.CarritoFeignClient;
import com.level_up.usuarios.dto.ActualizarUsuarioDTO;
import com.level_up.usuarios.dto.AgregarUsuarioDTO;
import com.level_up.usuarios.dto.UsuarioRetornoDTO;
import com.level_up.usuarios.enums.RolEnum;
import com.level_up.usuarios.exception.*;
import com.level_up.usuarios.model.UsuarioModel;
import com.level_up.usuarios.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.UUID;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class UsuarioService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final UsuarioRepository usuarioRepository;
    private final CarritoFeignClient carritoFeignClient;
    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // private final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private final long MAX_BYTES = 1_500_000;

    public UsuarioModel save(AgregarUsuarioDTO agregarUsuarioDTO) {
        try {
            if (usuarioRepository.existsByCorreo(agregarUsuarioDTO.getCorreo())) {
                throw new UsuarioSaveException("El correo ya se encuentra registrado.");
            }

            UsuarioModel nuevoUsuario = new UsuarioModel();

            if (usuarioRepository.existsByNombreUsuario(agregarUsuarioDTO.getNombreUsuario())) {
                throw new UsuarioSaveException("El nombre de usuario ya se encuentra registrado");
            }

            nuevoUsuario.setCorreo(agregarUsuarioDTO.getCorreo());
            String hash = passwordEncoder.encode(agregarUsuarioDTO.getContrasena());
            nuevoUsuario.setContrasena(hash);

            nuevoUsuario.setNombreUsuario(agregarUsuarioDTO.getNombreUsuario());
            nuevoUsuario.setNombre(agregarUsuarioDTO.getNombre());
            nuevoUsuario.setApellido(agregarUsuarioDTO.getApellido());
            nuevoUsuario.setFechaNacimiento(agregarUsuarioDTO.getFechaNacimiento());
            nuevoUsuario.setRol(RolEnum.USER.getValue());

            UsuarioModel usuarioGuardado = usuarioRepository.save(nuevoUsuario);

            try {
                carritoFeignClient.inicializarCarrito(usuarioGuardado.getIdUsuario());
            } catch (Exception errorFeign) {
                System.err.println("ADVERTENCIA: Fallo la inicializacion del carrito: " + errorFeign.getMessage());
            }

            return usuarioGuardado;

        } catch (DataAccessException e) {
            throw new UsuarioSaveException("Error al guardar el usuario: ", e);
        }
    }

    public UsuarioModel findById(Long id) {
        try {
            return usuarioRepository.findById(id)
                    .orElseThrow(() -> new UsuarioNotFoundException("El usuario con el ID: " + id + " no existe."));
        } catch (DataAccessException e) {
            throw new UsuarioNotFoundException("Error inesperado al buscar al usuario.", e);
        }
    }

    public UsuarioRetornoDTO iniciarSesion(String correo, String contrasena) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(correo, contrasena)
            );
        } catch (Exception e) {
            throw new UsuarioLoginException("Credenciales invalidas");
        }

        UsuarioModel usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        UsuarioRetornoDTO usuarioRetorno = new UsuarioRetornoDTO();
        String jwt = jwtService.generarToken(usuario);
        String publicURL = getImagenPerfil(usuario);

        usuarioRetorno.setIdUsuario(usuario.getIdUsuario());
        usuarioRetorno.setNombreUsuario(usuario.getNombreUsuario());
        usuarioRetorno.setNombre(usuario.getNombre());
        usuarioRetorno.setApellido(usuario.getApellido());
        usuarioRetorno.setCorreo(usuario.getCorreo());
        usuarioRetorno.setRol(usuario.getRol());
        usuarioRetorno.setImagenPerfilURL(publicURL);
        usuarioRetorno.setToken(jwt);

        return usuarioRetorno;
    }

    public UsuarioModel actualizarInformacionUsuario(Long idUsuario, ActualizarUsuarioDTO actualizarUsuarioDTO) {
        try {
            UsuarioModel usuarioEncontrado = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new UsuarioNotFoundException("No se ha encontrado el usuario con ID: " + idUsuario));

            if (actualizarUsuarioDTO.getNombre() != null && !actualizarUsuarioDTO.getNombre().isBlank()) {
                usuarioEncontrado.setNombre(actualizarUsuarioDTO.getNombre());
            }

            if (actualizarUsuarioDTO.getApellido() != null && !actualizarUsuarioDTO.getApellido().isBlank()) {
                usuarioEncontrado.setApellido(actualizarUsuarioDTO.getApellido());
            }

            if (actualizarUsuarioDTO.getContrasena() != null && !actualizarUsuarioDTO.getContrasena().isBlank()) {
                String contrasena = actualizarUsuarioDTO.getContrasena();
                if (contrasena.length() < 6) throw new UsuarioUpdateException("La contraseña debe tener al menos 6 caracteres.");
                if (contrasena.contains(" ")) throw new UsuarioUpdateException("La contraseña no puede contener espacios.");
                usuarioEncontrado.setContrasena(passwordEncoder.encode(contrasena));
            }

            if (actualizarUsuarioDTO.getNombreUsuario() != null &&
                    !actualizarUsuarioDTO.getNombreUsuario().isBlank() &&
                    !usuarioEncontrado.getNombreUsuario().equals(actualizarUsuarioDTO.getNombreUsuario())) {

                if (usuarioRepository.existsByNombreUsuario(actualizarUsuarioDTO.getNombreUsuario())) {
                    throw new UsuarioUpdateException("El nombre de usuario ya está en uso.");
                }
                usuarioEncontrado.setNombreUsuario(actualizarUsuarioDTO.getNombreUsuario());
            }

            return usuarioRepository.save(usuarioEncontrado);
        } catch (DataAccessException e) {
            throw new UsuarioUpdateException("Error al actualizar el usuario: " + e.getMessage(), e);
        }
    }

    public void eliminarUsuario(Long idUsuario) {
        try {
            UsuarioModel usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));
            usuarioRepository.delete(usuario);
        } catch (DataAccessException e) {
            throw new UsuarioDeleteException("Error al eliminar usuario: " + e.getMessage(), e);
        }
    }

    public UsuarioRetornoDTO actualizarImagenPerfilArchivo(Long idUsuario, MultipartFile imagen) {
        UsuarioModel usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioUpdateException("Usuario no encontrado ID: " + idUsuario));

        try {
            if (imagen == null || imagen.isEmpty()) {
                throw new UsuarioUpdateException("El archivo de imagen está vacío");
            }

            String nombreOriginal = imagen.getOriginalFilename();
            String formato = obtenerFormatoImagen(nombreOriginal);

            byte[] imagenBytes = comprimirYRedimensionarImagen(imagen.getBytes(), formato);

            borrarImagenAnterior(usuario.getImagenPerfilURL());

            String rutaImagen = guardarImagen(imagenBytes, formato);
            usuario.setImagenPerfilURL(rutaImagen);

            UsuarioModel usuarioGuardado = usuarioRepository.save(usuario);

            return convertirADTO(usuarioGuardado);

        } catch (IOException e) {
            throw new UsuarioUpdateException("Error al procesar el archivo: " + e.getMessage());
        }
    }

    public UsuarioRetornoDTO actualizarImagenPerfilUrl(Long idUsuario, String urlImagen) {
        UsuarioModel usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioUpdateException("Usuario no encontrado ID: " + idUsuario));

        if (urlImagen == null || urlImagen.isBlank()) {
            throw new UsuarioUpdateException("La URL es inválida");
        }

        try {
            URL url = new URL(urlImagen);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            String contentType = conn.getContentType();
            if (contentType != null && !contentType.startsWith("image/")) {
                // System.out.println("Warning: Content-Type no es imagen (" + contentType + ")");
            }

            String formato = obtenerFormatoImagen(url.getPath());
            byte[] imagenBytes;
            try (InputStream in = conn.getInputStream()) {
                imagenBytes = in.readAllBytes();
            }

            imagenBytes = comprimirYRedimensionarImagen(imagenBytes, formato);

            borrarImagenAnterior(usuario.getImagenPerfilURL());
            String rutaImagen = guardarImagen(imagenBytes, formato);
            usuario.setImagenPerfilURL(rutaImagen);

            UsuarioModel usuarioGuardado = usuarioRepository.save(usuario);

            return convertirADTO(usuarioGuardado);

        } catch (IOException e) {
            throw new UsuarioUpdateException("Error al descargar la imagen: " + e.getMessage());
        }
    }

    public String getImagenPerfil(UsuarioModel usuario) {
        String rutaLocal = usuario.getImagenPerfilURL();
        if (rutaLocal == null || rutaLocal.isBlank()) {
            return null;
        }

        Path path = Paths.get(rutaLocal);
        String nombreArchivo = path.getFileName().toString();

        return "/uploads/" + nombreArchivo;
    }

    private String obtenerFormatoImagen(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "jpg";
        }
        String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf('.') + 1).toLowerCase();
        if (extension.contains("?")) {
            extension = extension.split("\\?")[0];
        }
        switch (extension) {
            case "png": return "png";
            case "webp": return "webp";
            case "jpg":
            case "jpeg":
            default: return "jpg";
        }
    }

    private String guardarImagen(byte[] imagenBytes, String formato) throws IOException {
        String nombreArchivo = UUID.randomUUID() + "." + formato;

        Path rutaBase = Paths.get(uploadDir);
        Path rutaCompleta = rutaBase.resolve(nombreArchivo);

        Files.createDirectories(rutaCompleta.getParent());

        Files.write(rutaCompleta, imagenBytes);

        return rutaCompleta.toString();
    }

    private void borrarImagenAnterior(String rutaActual) {
        if (rutaActual != null && !rutaActual.isBlank()) {
            File archivoAntiguo = new File(rutaActual);
            if (archivoAntiguo.exists()) {
                archivoAntiguo.delete();
            }
        }
    }

    private byte[] comprimirYRedimensionarImagen(byte[] original, String formato) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(original);
        BufferedImage imagen = ImageIO.read(bis);
        if (imagen == null) throw new UsuarioUpdateException("El archivo no es una imagen válida.");

        int maxWidth = 1024;
        int maxHeight = 1024;
        int width = imagen.getWidth();
        int height = imagen.getHeight();
        if (width > maxWidth || height > maxHeight) {
            float ratio = Math.min((float) maxWidth / width, (float) maxHeight / height);
            int newWidth = Math.round(width * ratio);
            int newHeight = Math.round(height * ratio);
            Image tmp = imagen.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(newWidth, newHeight,
                    imagen.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
            imagen = resized;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(formato);
        if (!writers.hasNext()) throw new UsuarioUpdateException("No hay escritor disponible para formato: " + formato);
        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        if ("jpg".equalsIgnoreCase(formato) && param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.7f);
        }

        writer.setOutput(new MemoryCacheImageOutputStream(bos));
        writer.write(null, new IIOImage(imagen, null, null), param);
        writer.dispose();

        byte[] bytesFinales = bos.toByteArray();

        if (bytesFinales.length > MAX_BYTES) {
            if ("jpg".equalsIgnoreCase(formato)) {
                float quality = 0.6f;
                while (bytesFinales.length > MAX_BYTES && quality > 0.1f) {
                    try (ByteArrayOutputStream tempBos = new ByteArrayOutputStream();
                         MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(tempBos)) {

                        Iterator<ImageWriter> tempWriters = ImageIO.getImageWritersByFormatName(formato);
                        if (!tempWriters.hasNext()) break;
                        ImageWriter tempWriter = tempWriters.next();

                        ImageWriteParam tempParam = tempWriter.getDefaultWriteParam();
                        tempParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        tempParam.setCompressionQuality(quality);

                        tempWriter.setOutput(output);
                        tempWriter.write(null, new IIOImage(imagen, null, null), tempParam);
                        tempWriter.dispose();

                        bytesFinales = tempBos.toByteArray();
                    }
                    quality -= 0.1f;
                }
            } else if ("png".equalsIgnoreCase(formato) || "webp".equalsIgnoreCase(formato)) {
                int w = imagen.getWidth();
                int h = imagen.getHeight();
                float scale = 0.9f;
                while (bytesFinales.length > MAX_BYTES && w > 100 && h > 100) {
                    w = Math.round(w * scale);
                    h = Math.round(h * scale);

                    Image tmp = imagen.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                    BufferedImage resized = new BufferedImage(w, h,
                            imagen.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = resized.createGraphics();
                    g2d.drawImage(tmp, 0, 0, null);
                    g2d.dispose();
                    imagen = resized;

                    ByteArrayOutputStream tempBos = new ByteArrayOutputStream();
                    ImageIO.write(imagen, formato, tempBos);
                    bytesFinales = tempBos.toByteArray();
                }
            }
        }
        return bytesFinales;
    }

    private UsuarioRetornoDTO convertirADTO(UsuarioModel usuario) {
        UsuarioRetornoDTO dto = new UsuarioRetornoDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombreUsuario(usuario.getNombreUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreo(usuario.getCorreo());
        dto.setImagenPerfilURL(getImagenPerfil(usuario));
        return dto;
    }
}