import java.io.*;
import java.net.*;

public class Servidor {
    public static void main(String[] args) {
        try {
            // Crear el socket del servidor y esperar por conexiones de los clientes
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Servidor en espera de conexiones...");

            // Esperar a que se conecte el primer cliente
            Socket jugador1Socket = serverSocket.accept();
            System.out.println("Jugador 1 conectado");

            // Esperar a que se conecte el segundo cliente
            Socket jugador2Socket = serverSocket.accept();
            System.out.println("Jugador 2 conectado");

            // Crear los objetos JugadorHandler para manejar las comunicaciones con los jugadores
            JugadorHandler jugador1Handler = new JugadorHandler(jugador1Socket, "Jugador 1");
            JugadorHandler jugador2Handler = new JugadorHandler(jugador2Socket, "Jugador 2");

            // Establecer las referencias de los oponentes en los JugadorHandler
            jugador1Handler.setOponenteSocket(jugador2Socket);
            jugador2Handler.setOponenteSocket(jugador1Socket);

            // Iniciar los hilos de los JugadorHandler
            Thread jugador1Thread = new Thread(jugador1Handler);
            Thread jugador2Thread = new Thread(jugador2Handler);
            jugador1Thread.start();
            jugador2Thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class JugadorHandler implements Runnable {
    private Socket socket;
    private Socket oponenteSocket;
    private String jugador;

    public JugadorHandler(Socket socket, String jugador) {
        this.socket = socket;
        this.jugador = jugador;
    }

    public void setOponenteSocket(Socket oponenteSocket) {
        this.oponenteSocket = oponenteSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader oponenteReader = new BufferedReader(new InputStreamReader(oponenteSocket.getInputStream()));
            PrintWriter oponenteWriter = new PrintWriter(oponenteSocket.getOutputStream(), true);

            writer.println("¡Conexión exitosa! Esperando elección del oponente...");

            while (true) {
                // Leer la elección del jugador
                String choice = reader.readLine();
                System.out.println(jugador + ": " + choice);

                if (choice.equalsIgnoreCase("Salir")) {
                    break;
                }

                // Enviar la elección del jugador al oponente
                oponenteWriter.println(choice);

                // Leer la elección del oponente
                String oponenteChoice = oponenteReader.readLine();
                System.out.println("Oponente de " + jugador + ": " + oponenteChoice);

                if (oponenteChoice.equalsIgnoreCase("Salir")) {
                    break;
                }

                // Enviar la elección del oponente al jugador
                writer.println(oponenteChoice);

                // Determinar el ganador
                String resultado = determinarGanador(choice, oponenteChoice);

                // Enviar el resultado a ambos jugadores
                writer.println(resultado);
                oponenteWriter.println(resultado);
            }

            // Cerrar las conexiones
            reader.close();
            writer.close();
            socket.close();
            oponenteReader.close();
            oponenteWriter.close();
            oponenteSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String determinarGanador(String eleccionJugador1, String eleccionJugador2) {
        if (eleccionJugador1.equalsIgnoreCase(eleccionJugador2)) {
            return "Empate";
        } else if ((eleccionJugador1.equalsIgnoreCase("R") && eleccionJugador2.equalsIgnoreCase("T"))
                || (eleccionJugador1.equalsIgnoreCase("T") && eleccionJugador2.equalsIgnoreCase("P"))
                || (eleccionJugador1.equalsIgnoreCase("P") && eleccionJugador2.equalsIgnoreCase("R"))) {
            return jugador + " gana";
        } else {
            return "Oponente de " + jugador + " gana";
        }
    }
}
