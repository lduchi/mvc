package Controlador;

import java.awt.Image;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import Modelo.ModeloPersona;
import Modelo.Persona;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import Vista.VistaPersona;
import javax.xml.ws.Holder;

public class ControllerPersona {

    /**
     * **** NOTES **** USO DE HODER ---> La clase Holder es una clase genérica
     * que se utiliza como un contenedor para almacenar un valor de cualquier
     * tipo. ->Es comúnmente utilizada cuando se necesita pasar un valor por
     * referencia a un método o una función, en lugar de pasarlo por valor.
     */
    private ModeloPersona modelo;
    private VistaPersona vista;
    private String id_persona = "", criterio = "";
    JFileChooser jfc;

    public ControllerPersona(ModeloPersona modelo, VistaPersona vista) {
        this.modelo = modelo;
        this.vista = vista;
        vista.setVisible(true);
        vista.getLblAlerta1().setVisible(false);
        CargarPersonas();
    }

    public void iniciaControl() {
        vista.getBtnActualizar().addActionListener(l -> CargarPersonas());
        //Buttoms pantalla principal
        vista.getBtnBuscar().addActionListener(l -> buscar());
        vista.getBtnCrear().addActionListener(l -> abrirDialogo(1));
        vista.getBtnEditar().addActionListener(l -> abrirDialogo(2));
        //Buttoms pantalla secundaria
        vista.getBtnAceptar().addActionListener(l -> CrearEditarPersona());
        vista.getBtnCancelar().addActionListener(l -> cancelar());
        //Abrir examinar java
        vista.getBtnExaminar().addActionListener(l -> examinarFoto());
        //Recuperar datos de la tabla para moificar
        vista.getTblPersonas().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                verIdDatos(evt);
            }
        });

        vista.getBtnEliminar().addActionListener(l -> eliminar());

        //busqueda incremental
        vista.getTxtBuscar().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscar();
            }
        });
    }

    private void CargarPersonas() {
        // Control para consultar al modelo
        // Y luego en la vista
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        vista.getTblPersonas().setDefaultRenderer(Object.class, new ImagenTabla());
        vista.getTblPersonas().setRowHeight(50);

        // Para darle forma al modelo de la tabla
        DefaultTableModel mTabla;
        mTabla = (DefaultTableModel) vista.getTblPersonas().getModel();
        mTabla.setNumRows(0);

        List<Persona> listap = modelo.listarPersonas("");
        Holder<Integer> i = new Holder<>(0);

        // Uso de una expresion landa
        listap.stream().forEach(pe -> {
            String[] filaNueva = {pe.getIdPersona(), pe.getNombre(), pe.getApellido(),
                pe.getFechanacimiento().toString(), pe.getTelefono(), pe.getSexo(), String.valueOf(pe.getSueldo()),
                String.valueOf(pe.getCupo())};
            mTabla.addRow(filaNueva);
//            //Llenar imagen
            Image foto = pe.getFoto();
            if (foto != null) {
                Image nimg = foto.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                ImageIcon icono = new ImageIcon(nimg);
                render.setIcon(icono);
                vista.getTblPersonas().setValueAt(new JLabel(icono), i.value, 8);
            } else {
                vista.getTblPersonas().setValueAt(null, i.value, 8);
            }

            i.value++;
        });
    }

    private void verIdDatos(java.awt.event.MouseEvent evt) {
        id_persona = "";
        DefaultTableModel tm = (DefaultTableModel) vista.getTblPersonas().getModel();

        id_persona = String.valueOf(tm.getValueAt(vista.getTblPersonas().getSelectedRow(), 0));
    }

    private void cancelar() {
        vista.getDlgPersona().dispose();
        id_persona = "";
    }

    /**
     * ---> Para abrir un dialogo de editar o crear la persona
     */
    private void abrirDialogo(int op) {
        String titulo;
        if (op == 1) {
            titulo = "Crear Persona";
            vista.getDlgPersona().setName("C");
            vista.getTxtDni().setEnabled(true);
            limpiarDatos();
            activarJdialog(titulo);
        } else {
            if (id_persona.equals("")) {
                JOptionPane.showMessageDialog(vista, "Seleccione una persona");
            } else {
                titulo = "Editar Persona";
                vista.getDlgPersona().setName("E");
                vista.getTxtDni().setEnabled(false);
                activarJdialog(titulo);
                cargarDatos();
                id_persona = "";
            }
        }
    }

    /**
     * ---> Para abrir el panel de editar y mostrar todos los datos
     */
    private void cargarDatos() {
        limpiarDatos();
        ModeloPersona persona = new ModeloPersona();
        persona = persona.MostrarPersonaAEditar(id_persona);

        vista.getTxtDni().setText(persona.getIdPersona());
        vista.getTxtNombre().setText(persona.getNombre());
        vista.getTxtApellido().setText(persona.getApellido());
        vista.getJdcFechaNac().setDate(persona.getFechanacimiento());
        vista.getTxtTelefono().setText(persona.getTelefono());
        vista.getjComboBoxSexo().setSelectedItem(persona.getSexo());
        vista.getTxtSueldo().setText(persona.getSueldo() + "");
        vista.getTxtCupo().setText(persona.getCupo() + "");

        Holder<Integer> i = new Holder<>(0);
        Image foto = persona.getFoto();
        if (foto != null) {
            foto = foto.getScaledInstance(90, 120, Image.SCALE_SMOOTH);
            ImageIcon icono = new ImageIcon(foto);
            vista.getLbl_foto().setIcon(icono);
        } else {
            vista.getTblPersonas().setValueAt(null, i.value, 8);
        }
    }

    private void examinarFoto() {
        jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                Image imagen = ImageIO.read(jfc.getSelectedFile()).getScaledInstance(
                        vista.getLbl_foto().getWidth(),
                        vista.getLbl_foto().getHeight(),
                        Image.SCALE_DEFAULT);
                Icon icon = new ImageIcon(imagen);
                vista.getLbl_foto().setIcon(icon);
                vista.getLbl_foto().updateUI();
                // vista.getDlgPersona().setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(ControllerPersona.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void CrearEditarPersona() {
        //registrar
        if (datosNoVacios()) {
            if (vista.getDlgPersona().getName().contentEquals("C")) {
                ModeloPersona persona = new ModeloPersona();
                persona = recuperarDatos(persona);
                if (persona.CrearPersona()) {
                    JOptionPane.showMessageDialog(null,
                            "Persona creada satisfactoriamente.");
                    vista.getDlgPersona().dispose();
                    CargarPersonas();
                } else {
                    JOptionPane.showMessageDialog(vista,
                            "No se pudo crear persona error id repetido");
                }
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Faltan datos");
        }

        if (vista.getDlgPersona().getName().contentEquals("E")) {
            if (datosNoVacios()) {
                ModeloPersona persona = new ModeloPersona();
                persona = recuperarDatos(persona);

                if (persona.ActualizarDatos()) {
                    JOptionPane.showMessageDialog(null,
                            "Persona Modificada satisfactoriamente.");
                    vista.getDlgPersona().dispose();

                    CargarPersonas();
                } else {
                    JOptionPane.showMessageDialog(vista,
                            "No se pudo Modificar persona error base");
                }
            } else {
                JOptionPane.showMessageDialog(vista, "No se puede editar faltan datos");
            }
        }
    }

    //Recupera los datos para ser modificados o creados
    private ModeloPersona recuperarDatos(ModeloPersona per) {
        //INSERT
        String identificacion = vista.getTxtDni().getText();
        String nombres = vista.getTxtNombre().getText();
        String apellidos = vista.getTxtApellido().getText();
        Date fechaNac = vista.getJdcFechaNac().getDate();
        String telefono = vista.getTxtTelefono().getText();
        String sexo = vista.getjComboBoxSexo().getSelectedItem().toString();
        int sueldo = Integer.parseInt(vista.getTxtSueldo().getText());
        int cupo = Integer.parseInt(vista.getTxtCupo().getText());

        per.setIdPersona(identificacion);
        per.setNombre(nombres);
        per.setApellido(apellidos);
        per.setFechanacimiento(fechaNac);
        per.setTelefono(telefono);
        per.setSexo(sexo);
        per.setSueldo(sueldo);
        per.setCupo(cupo);
        
        if (vista.getLbl_foto().getIcon() != null) {
            ImageIcon iconImage = (ImageIcon) vista.getLbl_foto().getIcon();
            Image image = iconImage.getImage();
            per.setFoto(image);
        } else {
            per.setFoto(null);
        }

        return per;
    }

    private void eliminar() {
        if (id_persona.equals("")) {
            JOptionPane.showMessageDialog(vista, "Selecciona una persona");

        } else {
            int respuesta = 0;

            respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro?", "Eliminar!", JOptionPane.YES_NO_OPTION);
            if (respuesta == 0) {
                ModeloPersona persona = new ModeloPersona(id_persona, null, null, null, null, null, 0, 0, null);

                if (persona.deletePersona()) {
                    JOptionPane.showMessageDialog(vista, "Registro Eliminado");
                    id_persona = "";
                    CargarPersonas();
                } else {
                    JOptionPane.showMessageDialog(vista, "El registro no se elimino");
                    id_persona = "";
                }
            } else {
                JOptionPane.showMessageDialog(vista, "Cancelado");
                id_persona = "";
            }
        }
    }

    private boolean datosNoVacios() {
        return !vista.getTxtDni().getText().equals("") && !vista.getTxtNombre().getText().equals("") && !vista.getTxtApellido().getText().equals("")
                && !vista.getJdcFechaNac().toString().equals("") && !vista.getTxtTelefono().getText().equals("")
                && !vista.getjComboBoxSexo().getSelectedItem().equals("") && !vista.getTxtSueldo().getText().equals("")
                && !vista.getTxtCupo().getText().equals("");
    }

    private void buscar() {
        criterio = vista.getTxtBuscar().getText().trim();

        if (!criterio.equals("")) {
            llenarTablaBusqueda();
        } else {
            vista.getLblAlerta1().setVisible(false);
            CargarPersonas();
        }
    }

    private void llenarTablaBusqueda() {
        DefaultTableModel estructuraTabla;
        estructuraTabla = (DefaultTableModel) vista.getTblPersonas().getModel();
        estructuraTabla.setNumRows(0);
        List<Persona> listap = modelo.listarPersonas(criterio);

        Holder<Integer> i = new Holder<>(0);
        if (!listap.isEmpty()) {

            vista.getLblAlerta1().setVisible(false);

            listap.stream().forEach(persona -> {
                estructuraTabla.addRow(new Object[3]);
                vista.getTblPersonas()
                        .setValueAt(persona.getIdPersona(),
                                i.value, 0);
                vista.getTblPersonas()
                        .setValueAt(persona.getNombre(),
                                i.value, 1);
                vista.getTblPersonas()
                        .setValueAt(persona.getApellido(),
                                i.value, 2);
                vista.getTblPersonas()
                        .setValueAt(persona.getFechanacimiento(),
                                i.value, 3);
                vista.getTblPersonas()
                        .setValueAt(persona.getTelefono(),
                                i.value, 4);
                vista.getTblPersonas()
                        .setValueAt(persona.getSexo(),
                                i.value, 5);
                vista.getTblPersonas()
                        .setValueAt(persona.getSueldo(),
                                i.value, 6);
                vista.getTblPersonas()
                        .setValueAt(persona.getCupo(),
                                i.value, 7);
                //Llenar imagen
                Image foto = persona.getFoto();
                if (foto != null) {
                    foto = foto.getScaledInstance(50, 75, Image.SCALE_SMOOTH);
                    ImageIcon icono = new ImageIcon(foto);
                    DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
                    dtcr.setIcon(icono);
                    vista.getTblPersonas().setValueAt(new JLabel(icono), i.value, 8);

                } else {
                    vista.getTblPersonas().setValueAt(null, i.value, 8);
                }
                i.value++;
            });
        } else {
            vista.getLblAlerta1().setVisible(true);
        }
    }

    private void activarJdialog(String titulo) {
        vista.getDlgPersona().setTitle(titulo);
        vista.getDlgPersona().setSize(680, 330);
        vista.getDlgPersona().setLocationRelativeTo(vista);
        vista.getDlgPersona().setVisible(true);
    }

    private void limpiarDatos() {
        vista.getTxtDni().setText("");
        vista.getTxtNombre().setText("");
        vista.getTxtApellido().setText("");
        vista.getTxtTelefono().setText("");
        vista.getjComboBoxSexo().setSelectedItem(1);
        vista.getTxtSueldo().setText("");
        vista.getTxtCupo().setText("");
        vista.getLbl_foto().setIcon(null);
    }
}
