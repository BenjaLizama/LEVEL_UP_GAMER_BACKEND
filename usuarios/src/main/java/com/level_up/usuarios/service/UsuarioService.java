package com.level_up.usuarios.service;

import com.level_up.usuarios.client.CarritoFeignClient;
import com.level_up.usuarios.dto.ActualizarUsuarioDTO;
import com.level_up.usuarios.dto.AgregarUsuarioDTO;
import com.level_up.usuarios.dto.UsuarioRetornoDTO;
import com.level_up.usuarios.exception.*;
import com.level_up.usuarios.model.UsuarioModel;
import com.level_up.usuarios.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CarritoFeignClient carritoFeignClient;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
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

            String hash = PASSWORD_ENCODER.encode(agregarUsuarioDTO.getContrasena());
            nuevoUsuario.setContrasena(hash);

            nuevoUsuario.setNombreUsuario(agregarUsuarioDTO.getNombreUsuario());
            nuevoUsuario.setNombre(agregarUsuarioDTO.getNombre());
            nuevoUsuario.setApellido(agregarUsuarioDTO.getApellido());
            nuevoUsuario.setFechaNacimiento(agregarUsuarioDTO.getFechaNacimiento());

            UsuarioModel usuarioGuardado = usuarioRepository.save(nuevoUsuario);

            try {
                carritoFeignClient.inicializarCarrito(usuarioGuardado.getIdUsuario());
            } catch (Exception errorFeign) {
                System.err.println(
                        "ADVERTENCIA: El usuario " + usuarioGuardado.getIdUsuario() +
                        "se creo, pero fallo la inicializacion automatica del carrito: " +
                        errorFeign.getMessage()
                );
            }

            return usuarioGuardado;

        } catch (DataAccessException e) {
            throw new UsuarioSaveException("Error al guardar el usuario: ", e);
        }
    }

    public UsuarioModel findById(Long id) {
        try {
            return usuarioRepository.findById(id)
                    .orElseThrow(() ->
                            new UsuarioNotFoundException("El usuario con el ID: " + id + " no existe."));

        } catch (DataAccessException e) {
            throw new UsuarioNotFoundException("Error inesperado al buscar al usuario.", e);
        }
    }

    public UsuarioModel validarCredenciales(String correo, String contrasena) {
        UsuarioModel usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsuarioNotFoundException("Correo o contraseña incorrectos."));

        if (!PASSWORD_ENCODER.matches(contrasena, usuario.getContrasena())) {
            throw new UsuarioLoginException("Contraseña incorrecta.");
        }

        return usuario;
    }

    public UsuarioRetornoDTO iniciarSesion(String correo, String contrasena) {
        UsuarioModel usuario = validarCredenciales(correo, contrasena);
        UsuarioRetornoDTO usuarioRetorno = new UsuarioRetornoDTO();

        String jwt = jwtService.generarToken(usuario);
        String publicURL = getImagenPerfil(usuario);

        usuarioRetorno.setIdUsuario(usuario.getIdUsuario());
        usuarioRetorno.setNombreUsuario(usuario.getNombreUsuario());
        usuarioRetorno.setNombre(usuario.getNombre());
        usuarioRetorno.setApellido(usuario.getApellido());
        usuarioRetorno.setCorreo(usuario.getCorreo());
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
                if (contrasena.length() < 6) {
                    throw new UsuarioUpdateException("La contraseña debe tener al menos 6 caracteres.");
                }
                if (contrasena.contains(" ")) {
                    throw new UsuarioUpdateException("La contraseña no puede contener espacios.");
                }
                usuarioEncontrado.setContrasena(PASSWORD_ENCODER.encode(contrasena));
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
                    .orElseThrow(() -> new UsuarioNotFoundException("No se ha encontrado el usuario con ID: " + idUsuario));

            usuarioRepository.delete(usuario);
        } catch (DataAccessException e) {
            throw new UsuarioDeleteException("Error inesperado al eliminar el usuario: " + e.getMessage(), e);
        }
    }

    public UsuarioModel actualizarImagenPerfil(Long idUsuario, MultipartFile imagen, String urlImagen) {
        UsuarioModel usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioUpdateException("No se ha encontrado el usuario con ID: " + idUsuario));

        byte[] imagenBytes;
        String formato;

        try {
            String imagenActual = usuario.getImagenPerfilURL();

            if (imagen != null) {
                // Validar tipo de archivo
                formato = obtenerFormatoImagen(imagen.getOriginalFilename());
                imagenBytes = imagen.getBytes();
            } else {
                if (urlImagen == null || urlImagen.isBlank()) {
                    throw new UsuarioUpdateException("Debe enviar una imagen o URL válida.");
                }
                URL url = new URL(urlImagen);
                URLConnection conn = url.openConnection();
                String contentType = conn.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new UsuarioUpdateException("La URL no apunta a una imagen válida.");
                }
                formato = obtenerFormatoImagen(url.getPath());
                try (InputStream in = conn.getInputStream()) {
                    imagenBytes = in.readAllBytes();
                }
            }

            // Comprimir/redimensionar segun tipo
            imagenBytes = comprimirYRedimensionarImagen(imagenBytes, formato);

            // Eliminar la imagen antigua si existe
            if (imagenActual != null && !imagenActual.isBlank()) {
                File archivoAntiguo = new File(imagenActual);
                if (archivoAntiguo.exists()) {
                    archivoAntiguo.delete();
                }
            }

            // Guardar la imagen en el servidor
            String rutaImagen = guardarImagen(imagenBytes, formato);
            usuario.setImagenPerfilURL(rutaImagen);

            return usuarioRepository.save(usuario);
        } catch (IOException e) {
            throw new UsuarioUpdateException("Error al procesar la imagen: " + e.getMessage());
        }
    }

    public String getImagenPerfil(UsuarioModel usuario) {
        String rutaLocal = usuario.getImagenPerfilURL();

        if (rutaLocal == null || rutaLocal.isBlank()) {
            return  null;
        }

        Path path = Paths.get(rutaLocal);
        String nombreArchivo = path.getFileName().toString();

        return "/profile-images/" + nombreArchivo;
    }

    private String obtenerFormatoImagen(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "jpg";
        }
        String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "png":
                return "png";
            case "webp":
                return "webp";
            case "jpg":
            case "jpeg":
            default:
                return "jpg";
        }
    }

    private String guardarImagen(byte[] imagenBytes, String formato) throws IOException {
        String nombreArchivo = UUID.randomUUID() + "." + formato;
        Path ruta = Paths.get("uploads/" + nombreArchivo);
        Files.createDirectories(ruta.getParent());
        Files.write(ruta, imagenBytes);
        return ruta.toString();
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
        if (!writers.hasNext()) throw new UsuarioUpdateException("No hay escritor de imágenes disponible para formato: " + formato);
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

}
