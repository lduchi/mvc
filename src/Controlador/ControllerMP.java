package Controlador;

import Modelo.ModeloPersona;
import Vista.VistaPersona;
import Vista.VistaMP;

public class ControllerMP {

    private VistaMP vista;

    public ControllerMP(VistaMP v) {
        this.vista = v;
        vista.setVisible(true);
    }

    public void iniciaControl() {
        vista.getjMenuItemCrearPersona().addActionListener(l -> crudPersona());
        vista.getPersona_Button().addActionListener(l -> crudPersona());
    }

    private void crudPersona() {
        ModeloPersona m = new ModeloPersona();
        VistaPersona v = new VistaPersona();
        vista.getjDesktop().add(v);
        ControllerPersona c = new ControllerPersona(m, v);
        c.iniciaControl();
    }
}
