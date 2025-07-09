import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import org.json.*;

public class WeatherApp extends JFrame {
    private JTextField cityField;
    private JEditorPane resultArea;
    private JButton getWeatherButton;
    private JCheckBox unitToggle;
    private boolean useCelsius = true;

    private final String API_KEY = "0532cc09d1c660ac365146238280539f"; // ✅ Replace with your actual key

    public WeatherApp() {
        setTitle("🌦️ Weather App");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(30, 136, 229));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel cityLabel = new JLabel("Enter City:");
        cityLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cityLabel.setForeground(Color.WHITE);

        cityField = new JTextField();
        cityField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        getWeatherButton = new JButton("Get Weather");
        getWeatherButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        getWeatherButton.setBackground(Color.WHITE);
        getWeatherButton.setForeground(new Color(30, 136, 229));

        unitToggle = new JCheckBox("Use °F");
        unitToggle.setBackground(new Color(30, 136, 229));
        unitToggle.setForeground(Color.WHITE);
        unitToggle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        unitToggle.addActionListener(e -> useCelsius = !unitToggle.isSelected());

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(getWeatherButton, BorderLayout.CENTER);
        rightPanel.add(unitToggle, BorderLayout.SOUTH);

        topPanel.add(cityLabel, BorderLayout.WEST);
        topPanel.add(cityField, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // ===== RESULT AREA =====
        resultArea = new JEditorPane();
        resultArea.setContentType("text/html");
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(245, 245, 245));
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        resultArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        resultArea.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Add to frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Button action
        getWeatherButton.addActionListener(e -> {
            String city = cityField.getText().trim();
            if (!city.isEmpty()) {
                getWeather(city);
            }
        });
    }

    private void getWeather(String city) {
        try {
            String units = useCelsius ? "metric" : "imperial";
            String tempUnit = useCelsius ? "°C" : "°F";

            // --- Current Weather API ---
            String currentUrl = "https://api.openweathermap.org/data/2.5/weather?q=" +
                    URLEncoder.encode(city, "UTF-8") + "&units=" + units + "&appid=" + API_KEY;

            URI currentUri = new URI(currentUrl);
            HttpURLConnection currentConn = (HttpURLConnection) currentUri.toURL().openConnection();
            BufferedReader currentReader = new BufferedReader(new InputStreamReader(currentConn.getInputStream()));
            StringBuilder currentSb = new StringBuilder();
            String line;

            while ((line = currentReader.readLine()) != null) {
                currentSb.append(line);
            }

            JSONObject currentResponse = new JSONObject(currentSb.toString());
            JSONObject main = currentResponse.getJSONObject("main");
            JSONObject weather = currentResponse.getJSONArray("weather").getJSONObject(0);
            String iconCode = weather.getString("icon");

            // Show icon popup
            ImageIcon icon = new ImageIcon(new URL("https://openweathermap.org/img/wn/" + iconCode + "@2x.png"));
            JLabel iconLabel = new JLabel(icon);
            JOptionPane.showMessageDialog(this, iconLabel, "Weather Icon", JOptionPane.PLAIN_MESSAGE);

            // Get date & time
            String date = LocalDate.now().toString();
            String time = LocalTime.now().withNano(0).toString();
        String output = "<html><body style='font-family: Segoe UI; font-size: 14px;'>"
        + "<b>📅 Date:</b> " + date + "&nbsp;&nbsp;&nbsp; <b>⏰ Time:</b> " + time + "<br><br>"
        + "<b>📍 City:</b> " + currentResponse.getString("name") + "<br>"
        + "<b>🌡️ Temp:</b> " + main.getDouble("temp") + " " + tempUnit + "<br>"
        + "<b>💧 Humidity:</b> " + main.getInt("humidity") + "%<br>"
        + "<b>☁️ Description:</b> " + weather.getString("description") + "<br><br>"
        + "<b>📆 3-Day Forecast:</b><br>";


            // --- Forecast API ---
            String forecastUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" +
                    URLEncoder.encode(city, "UTF-8") + "&units=" + units + "&appid=" + API_KEY;

            URI forecastUri = new URI(forecastUrl);
            HttpURLConnection forecastConn = (HttpURLConnection) forecastUri.toURL().openConnection();
            BufferedReader forecastReader = new BufferedReader(new InputStreamReader(forecastConn.getInputStream()));
            StringBuilder forecastSb = new StringBuilder();

            while ((line = forecastReader.readLine()) != null) {
                forecastSb.append(line);
            }

            JSONObject forecastResponse = new JSONObject(forecastSb.toString());
            JSONArray list = forecastResponse.getJSONArray("list");

            output += "📆 3-Day Forecast:\n";

            Set<String> addedDates = new HashSet<>();
            for (int i = 0; i < list.length(); i++) {
                JSONObject obj = list.getJSONObject(i);
                String dtTxt = obj.getString("dt_txt");
                String forecastDate = dtTxt.split(" ")[0];
                if (!addedDates.contains(forecastDate) && addedDates.size() < 3) {
                    JSONObject temp = obj.getJSONObject("main");
                    JSONObject desc = obj.getJSONArray("weather").getJSONObject(0);
                    double dayTemp = temp.getDouble("temp");
                    String description = desc.getString("description");
                  output += "➤ <b>" + forecastDate + ":</b> " + dayTemp + " " + tempUnit + ", " + description + "<br>";
                }
                   output += "</body></html>";
            }

            resultArea.setText(output);
        } catch (Exception ex) {
            resultArea.setText("⚠️ Error fetching weather data.\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherApp().setVisible(true));
    }
}
