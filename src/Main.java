import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final Map<String, String> VariablesCurrency = new HashMap<>();

    public static void printAvailableCurrencies(JsonObject conversionRates) {
        System.out.println("Monedas disponibles:");
        for (String currency : conversionRates.keySet()) {
            String name = VariablesCurrency.getOrDefault(currency, "No Encontrado");
            System.out.printf("%s: %s\n", currency, name);
        }
        System.out.println();
    }
    public static double convert(double amount, double rate) {
        return amount * rate;
    }
    public static int getIntInput(Scanner scanner) {
        while (true) {
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            } else {
                System.out.println("Entrada inválida. Por favor, ingrese un número entero.");
                scanner.next();
            }
        }
    }
    public static double getDoubleInput(Scanner scanner) {
        while (true) {
            if (scanner.hasNextDouble()) {
                return scanner.nextDouble();
            } else {
                System.out.println("Entrada inválida. Por favor, ingrese un número decimal.");
                scanner.next();
            }
        }
    }

    public static String getCurrencyInput(Scanner scanner, JsonObject conversionRates) {
        while (true) {
            String input = scanner.next().toUpperCase();
            if (VariablesCurrency.containsKey(input) && conversionRates.has(input)) {
                return input;
            } else {
                System.out.println("Moneda no válida. Por favor, ingrese una moneda válida.");
                printAvailableCurrencies(conversionRates);
            }
        }
    }
    public static void menu_Convert(String responseBody) {
        Convert_History history = new Convert_History();
        Scanner scanner = new Scanner(System.in);
        Gson gson = new Gson();
        JsonObject jsonObject = gson.<JsonObject>fromJson(responseBody, JsonObject.class);
        JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");

        try (InputStream inputStream = Main.class.getResourceAsStream("/VariablesCurrency.json")) {
            if (inputStream == null) {
                throw new RuntimeException("No se encontro el archivo VariablesCurrency.json.");
            }
            try (InputStreamReader reader = new InputStreamReader(inputStream)) {
                JsonObject VariablesCurrencyObject = JsonParser.parseReader(reader).getAsJsonObject();
                for (Map.Entry<String, com.google.gson.JsonElement> entry : VariablesCurrencyObject.entrySet()) {
                    VariablesCurrency.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al leer el archivo .json: " + e.getMessage(), e);
            return;
        }

        while (true) {
            System.out.println("Por Favor Seleccione una opción:");
            System.out.println("1. Convertir Monedas");
            System.out.println("2. Ver historial de Conversiones Realizadas");
            System.out.println("3. Salir");

            int option = getIntInput(scanner);
            if (option == 3) {
                break;
            }

            switch (option) {
                case 1:
                    System.out.println("Ingrese la cantidad a convertir:");
                    double amount = getDoubleInput(scanner);

                    System.out.println("Seleccione la moneda de origen:");
                    printAvailableCurrencies(conversionRates);
                    String fromCurrency = getCurrencyInput(scanner, conversionRates);
                    System.out.println("Seleccione la moneda a la cual desea convertir:");
                    printAvailableCurrencies(conversionRates);
                    String toCurrency = getCurrencyInput(scanner, conversionRates);
                    if (conversionRates.has(fromCurrency) && conversionRates.has(toCurrency)) {
                        double fromRate = conversionRates.get(fromCurrency).getAsDouble();
                        double toRate = conversionRates.get(toCurrency).getAsDouble();
                        double rate = toRate / fromRate;
                        double convertedAmount = convert(amount, rate);
                        String fromCurrencyName = VariablesCurrency.getOrDefault(fromCurrency, "Desconocido");
                        String toCurrencyName = VariablesCurrency.getOrDefault(toCurrency, "Desconocido");
                        System.out.printf("$ %.2f %s (%s) a %s (%s) $ %.2f\n", amount, fromCurrency, fromCurrencyName, toCurrency, toCurrencyName, convertedAmount);
                        history.addHistory(amount, fromCurrency, toCurrency, rate, convertedAmount);
                    } else {
                        System.out.println("Moneda no válida.");
                    }
                    break;

                case 2:
                    history.showHistory();
                    break;

                default:
                    System.out.println("Opción inválida");
                    break;
            }
        }
    }
    public static void main(String[] args) {
        String apiKey = "3762c6f9185a16aeea238975";
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/USD";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(Main::menu_Convert)
                .join();
    }
}