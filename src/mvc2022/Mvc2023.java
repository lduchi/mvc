package mvc2022;

import Vista.*;
import Controlador.*;

public class Mvc2023 {

    public static void main(String[] args) {
        VistaMP vista = new VistaMP();
        ControllerMP controller = new ControllerMP(vista);
        controller.iniciaControl();
    }
}
