package com.project;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class CtrlLayoutConnected implements AppData.MessageListener{
    private AppData appData;
    private boolean turno = true;
    @FXML
    private Label serverAddressLabel;

    @FXML
    private Label clientIdLabel;
    @FXML
    private Button btn00,btn01, btn02, btn03, btn10, btn11, btn12, btn13, btn20, btn21, btn22, btn23, btn30, btn31, btn32,btn33;
    @FXML
    private ImageView img00, img10, img20, img30, img01, img02, img03, img11, img12, img13, img21, img22, img23, img31, img32, img33;
    private List<String> images = new ArrayList<>();
    private String pulsado1 = "", pulsado2 = "", pulsado1r = "", pulsado2r = "";
    private ImageView pulsado_i1, pulsado_i2, pulsado_i1r, pulsado_i2r;
    private Integer parejas =0;
    private String button1,button2;
    private int pul;
    
    public void realizarAccionParaBoton(String nombreBoton, boolean visible) {
        Button clickedButton;
        switch (nombreBoton) {
            case "btn00":
                clickedButton = btn00;
                break;
            case "btn01":
                clickedButton = btn01;
                break;
            case "btn02":
                clickedButton = btn02;
                break;
            case "btn03":
                clickedButton = btn03;
                break;
            case "btn10":
                clickedButton = btn10;
                break;
            case "btn11":
                clickedButton = btn11;
                break;
            case "btn12":
                clickedButton = btn12;
                break;
            case "btn13":
                clickedButton = btn13;
                break;
            case "btn20":
                clickedButton = btn20;
                break;
            case "btn21":
                clickedButton = btn21;
                break;
            case "btn22":
                clickedButton = btn22;
                break;
            case "btn23":
                clickedButton = btn23;
                break;
            case "btn30":
                clickedButton = btn30;
                break;
            case "btn31":
                clickedButton = btn31;
                break;
            case "btn32":
                clickedButton = btn32; 
                break;
            case "btn33":
                clickedButton = btn33;
                break;
            default:
                // Acciones para casos no manejados
                System.out.println("Botón no reconocido: " + nombreBoton);
                return; // Salir del método si el botón no es reconocido
        }
        System.out.println("cc");
        ImageView imageView = (ImageView) clickedButton.getGraphic();
        imageView.setVisible(true);
        System.out.println("aaaaaaaaaaa");
        pul++;
        if(pul == 2){
            
            pul = 0;
        }
    }

    @FXML
    private void handleDisconnect(ActionEvent event) {
        AppData appData = AppData.getInstance();
        appData.disconnectFromServer();
        asignarImagenes();
        turno = true;
    }


    public void initialize() {
        appData = AppData.getInstance();
        appData.addMessageListener(this);
        updateInfo();
      
        loadImages();
        appData.images = images;
        
        asignarImagenes();
    }

    private void loadImages() {
        // Agregar nombres de tus archivos de imagen a la lista 'images'
        images.add("circle.png");
        images.add("triangle.png");
        images.add("circle.png");
        images.add("heart.png");

        images.add("star.png");
        images.add("triangle.png");
        images.add("star.png");
        images.add("heartP.png");

        images.add("circleR.png");
        images.add("triangleN.png");
        images.add("circleR.png");
        images.add("heart.png");

        images.add("starM.png");
        images.add("triangleN.png");
        images.add("starM.png");
        images.add("heartP.png");

        // ... (continuar con otras imágenes)
    }
    
    @FXML
    private void handleButtonClick(ActionEvent event) {
        if (!turno) {
            return; // Ignorar eventos si no es el turno
        }   
            if (pulsado1.equals("")){
                Button clickedButton = (Button) event.getSource();
                button1 = clickedButton.getId().toString();
                ImageView imageView = (ImageView) clickedButton.getGraphic();
                imageView.setVisible(true);
                pulsado1 = imageView.getImage().getUrl();
                pulsado_i1= imageView;
            }
            else{
                Button clickedButton = (Button) event.getSource();
                button2 = clickedButton.getId().toString();
                if (!(button2 == button1)){                
                    ImageView imageView = (ImageView) clickedButton.getGraphic();
                    imageView.setVisible(true);
                    pulsado2 = imageView.getImage().getUrl();
                    pulsado_i2= imageView;
                    if (!pulsado1.equals(pulsado2)){
                        Timeline timeline = new Timeline(
                        new KeyFrame(Duration.seconds(0.5), e -> {
                            pulsado_i2.setVisible(false);
                            pulsado_i1.setVisible(false);
                            pulsado1 = "";pulsado2 = "";
                        })
                        );
                        timeline.play();
                        System.out.println(button2);
                        System.out.println(button1);
                        if (appData.selectedClientIndex == null) {
                            appData.send(button1, button2);
                            turno = false;
                        }
                    }
                    else{
                        parejas +=1;
                        pulsado1 = "";pulsado2 = "";
                    if (appData.selectedClientIndex == null) {
                            appData.send(button1, button2);
                            turno = false;
                        }
                        if (parejas == 8){
                            asignarImagenes();
                            turno = false;
                        }
                    }
                }
            }
    
        }
   
    private void assignImageToButton(Button button, ImageView imageView, String imageName) {
        String imagePath ="src/main/resources/assets/images/" +imageName;
        Image image = new Image("file:" + imagePath);
        imageView.setImage(image);
        imageView.setVisible(false);
    }
    
    private void asignarImagenes() {
        assignImageToButton(btn00, img00, images.get(0));
        assignImageToButton(btn01, img01, images.get(4));
        assignImageToButton(btn02, img02, images.get(8));
        assignImageToButton(btn03, img03, images.get(12));

        assignImageToButton(btn10, img10, images.get(1));
        assignImageToButton(btn11, img11, images.get(5));
        assignImageToButton(btn12, img12, images.get(9));
        assignImageToButton(btn13, img13, images.get(13));

        assignImageToButton(btn20, img20, images.get(2));
        assignImageToButton(btn21, img21, images.get(6));
        assignImageToButton(btn22, img22, images.get(10));
        assignImageToButton(btn23, img23, images.get(14));

        assignImageToButton(btn30, img30, images.get(3));
        assignImageToButton(btn31, img31, images.get(7));
        assignImageToButton(btn32, img32, images.get(11));
        assignImageToButton(btn33, img33, images.get(15));
    }
   

    
    public void updateInfo() {
        AppData appData = AppData.getInstance();
        serverAddressLabel.setText(appData.getName());
        clientIdLabel.setText(appData.getRName());
    }

    @Override
    public void onMessageReceived(String message) {
        JSONObject data = new JSONObject(message);
        String type = data.getString("type");
        switch (type) {
            case "named":
                appData.setRName(data.getString("flutter"));
                updateInfo();
                break;
            case "broadcast":
            String to = data.getString("from");
            if (to.equals("flutter")){
                serverAddressLabel.setText(appData.getRName());
                clientIdLabel.setText(appData.getName());
                int mov1 = data.getInt("value1");
                realizarAccionSegunPosicion(mov1);
                int mov2 = data.getInt("value2");
                realizarAccionSegunPosicion(mov2);
                serverAddressLabel.setText(appData.getName());
                clientIdLabel.setText(appData.getRName());
            }

            break;
        }
    }
    private void realizarAccionSegunPosicion(int pos) {
        Button clickedButton;
        switch (pos) {
            case 0:
                clickedButton = btn00;
                break;
            case 1:
                clickedButton = btn01;
                break;
            case 2:
                clickedButton = btn02;
                break;
            case 3:
                clickedButton = btn03;
                break;
            case 4:
                clickedButton = btn10;
                break;
            case 5:
                clickedButton = btn11;
                break;
            case 6:
                clickedButton = btn12;
                break;
            case 7:
                clickedButton = btn13;
                break;
            case 8:
                clickedButton = btn20;
                break;
            case 9:
                clickedButton = btn21;
                break;
            case 10:
                clickedButton = btn22;
                break;
            case 11:
                clickedButton = btn23;
                break;
            case 12:
                clickedButton = btn30;
                break;
            case 13:
                clickedButton = btn31;
                break;
            case 14:
                clickedButton = btn32;
                break;
            case 15:
                clickedButton = btn33;
                break;
            default:
                mostrarMensaje("Posición no reconocida");
                return;
        }
        if (pulsado1r.equals("")){
            ImageView imageView = (ImageView) clickedButton.getGraphic();
            imageView.setVisible(true);
            pulsado1r= imageView.getImage().getUrl();
            pulsado_i1r= imageView;
        }else{
            ImageView imageView = (ImageView) clickedButton.getGraphic();
            imageView.setVisible(true);
            pulsado2r =imageView.getImage().getUrl();
            pulsado_i2r = imageView;
            if (!pulsado1r.equals(pulsado2r)){
                Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    pulsado_i2r.setVisible(false);
                    pulsado_i1r.setVisible(false);
                    pulsado1r = "";pulsado2r = "";
                    
                })
                );
                timeline.play();
                turno = true;
                
            }else{
                turno = true;
                parejas +=1;
                pulsado1r = "";pulsado2r = "";
                if (parejas == 8){
                    asignarImagenes();
                    turno = false;
                }
            }
        }
       
        // Resto del código...
    }
    private void mostrarMensaje(String mensaje) {
        // Puedes mostrar el mensaje en un Label, en un cuadro de diálogo, o hacer lo que desees
        System.out.println(mensaje);
    }
}
