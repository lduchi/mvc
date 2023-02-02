package Modelo;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.postgresql.util.Base64;

public class ModeloPersona extends Persona {

    ConectPG conpg = new ConectPG();

    public ModeloPersona() {
    }

    public ModeloPersona(String idPersona, String nombre, String apellido, Date fechanacimiento, String telefono, String sexo, int sueldo, int cupo, Image foto) {
        super(idPersona, nombre, apellido, fechanacimiento, telefono, sexo, sueldo, cupo, foto);
    }

    public List<Persona> listarPersonas(String filtro) {
        //--> No es recomendable usar un select *. Solo sacar  la informacion que es necesaria mostrar.
        String sql = "select * from persona where "; //Campos de la base de datos.
        sql += " UPPER(idpersona) like UPPER('%" + filtro + "%') ";
        sql += "OR UPPER(nombres) like UPPER('%" + filtro + "%') ";
        sql += "OR UPPER(apellidos) like UPPER('%" + filtro + "%') ";
        ResultSet rs = conpg.consulta(sql);
        List<Persona> lista = new ArrayList<Persona>();
        try {
            while (rs.next()) {
                Persona pe = new Persona();
                pe.setIdPersona(rs.getString("idpersona"));
                pe.setNombre(rs.getString("nombres"));
                pe.setApellido(rs.getString("apellidos"));
                pe.setFechanacimiento(rs.getDate("fechanacimiento"));
                pe.setTelefono(rs.getString("telefono"));
                pe.setSexo(rs.getString("sexo"));
                pe.setSueldo(rs.getInt("sueldo"));
                pe.setCupo(rs.getInt("cupo"));
                byte[] bf = rs.getBytes("foto");
                if (bf != null) {
                    bf = Base64.decode(bf, 0, bf.length);
                    try {
                        pe.setFoto(obtenerImagen(bf));
                    } catch (IOException ex) {
                        pe.setFoto(null);
                        Logger.getLogger(ModeloPersona.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    pe.setFoto(null);
                }
                lista.add(pe);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ModeloPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            rs.close();
            return lista;
        } catch (SQLException ex) {
            Logger.getLogger(ModeloPersona.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean CrearPersona() {
        String fotoP = null;

        BufferedImage img = imgBimage(getFoto());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(img, "PNG", bos);
            byte[] imgb = bos.toByteArray();
            fotoP = Base64.encodeBytes(imgb);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        String sql = "INSERT INTO persona (idpersona, nombres, apellidos, fechanacimiento, telefono, sexo, sueldo, cupo, foto)";

        Date fechaNac = new java.sql.Date(((Date) getFechanacimiento()).getTime());
        sql += " VALUES ('" + getIdPersona() + "','" + getNombre() + "','" + getApellido() + "','" + fechaNac
                + "','" + getTelefono() + "','" + getSexo() + "','" + getSueldo() + "','" + getCupo() + "','" + fotoP + "')";
        return conpg.accion(sql);

        //Otra forma de hacer el insert
//        sql += "VALUES (?,?,?,?,?,?,?,?,?)";
//
//        try {
//            PreparedStatement ps = conpg.con.prepareStatement(sql);
//            ps.setString(1, getIdPersona());
//            ps.setString(2, getNombre());
//            ps.setString(3, getApellido());
//            ps.setDate(4, new java.sql.Date(((Date) getFechanacimiento()).getTime()));
//            ps.setString(5, getTelefono());
//            ps.setString(6, getSexo());
//            ps.setInt(7, getSueldo());
//            ps.setInt(8, getCupo());
//            ps.setBinaryStream(9, getImageFile(), getLength());
//            ps.executeUpdate();
//            ps.close();
//            return true;
//        } catch (SQLException ex) {
//            Logger.getLogger(ModeloPersona.class.getName()).log(Level.SEVERE, null, ex);
//            return false;
//        }
    }

    public boolean ActualizarDatos() {
        String fotoP = null;
        if (getFoto() != null) {
            BufferedImage img = imgBimage(getFoto());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            try {
                ImageIO.write(img, "PNG", bos);
                byte[] imgb = bos.toByteArray();
                fotoP = Base64.encodeBytes(imgb);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        Date fechaNac = new java.sql.Date(((Date) getFechanacimiento()).getTime());

        String sql = "UPDATE persona SET nombres = '" + getNombre() + "', apellidos = '" + getApellido() + "', fechanacimiento = '" + fechaNac
                + "', telefono = '" + getTelefono() + "', sexo = '" + getSexo() + "', sueldo = '" + getSueldo() + "', cupo = '" + getCupo() + "', foto = '" + fotoP + "'";
        sql += "WHERE idpersona = '" + getIdPersona() + "';";
        System.out.println("SENTENCIA " + sql);
        return conpg.accion(sql);
    }

    public ModeloPersona MostrarPersonaAEditar(String id) {
        String sql = "select * from persona where idpersona = '" + id + "'";
        ResultSet rs = conpg.consulta(sql);
        ModeloPersona persona = new ModeloPersona();
        try {
            while (rs.next()) {
                persona.setIdPersona(rs.getString("idpersona"));
                persona.setNombre(rs.getString("nombres"));
                persona.setApellido(rs.getString("apellidos"));
                persona.setFechanacimiento(rs.getDate("fechanacimiento"));
                persona.setTelefono(rs.getString("telefono"));
                persona.setSexo(rs.getString("sexo"));
                persona.setSueldo(rs.getInt("sueldo"));
                persona.setCupo(rs.getInt("cupo"));
                byte[] bf = rs.getBytes("foto");
                if (bf != null) {
                    bf = Base64.decode(bf, 0, bf.length);
                    try {
                        persona.setFoto(obtenerImagen(bf));
                    } catch (IOException ex) {
                        persona.setFoto(null);
                        Logger.getLogger(ModeloPersona.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    persona.setFoto(null);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ModeloPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            rs.close();//cierro conexion BD
        } catch (SQLException ex) {
            Logger.getLogger(ModeloPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
        return persona;
    }

    public boolean deletePersona() {
        String sql = "DELETE FROM persona WHERE idpersona='" + getIdPersona() + "';";
        return conpg.accion(sql);
    }

    private BufferedImage imgBimage(Image img) {
        //Compruebo que no ya un buferrimage
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bi = new BufferedImage(
                img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGR = bi.createGraphics();
        bGR.drawImage(img, 0, 0, null);
        bGR.dispose();
        return bi;
    }

    private Image obtenerImagen(byte[] bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Iterator it = ImageIO.getImageReadersByFormatName("png");
        ImageReader reader = (ImageReader) it.next();
        Object source = bis;

        ImageInputStream iis = ImageIO.createImageInputStream(source);
        reader.setInput(iis, true);

        ImageReadParam param = reader.getDefaultReadParam();
        param.setSourceSubsampling(1, 1, 0, 0);

        return reader.read(0, param);
    }
}
