import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);

            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputWriter = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            String serverMessage = inputReader.readLine();
            System.out.println("Servidor: " + serverMessage);

            String choice;
            do {
                System.out.print("Elige una opción (Roca [R], Papel [P], Tijeras [T], o Salir): ");
                choice = consoleReader.readLine();

                // Enviar la elección al servidor
                outputWriter.println(choice);

                // Recibir el resultado del juego
                serverMessage = inputReader.readLine();
                System.out.println("Servidor: " + serverMessage);
            } while (!choice.equals("Salir"));

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
