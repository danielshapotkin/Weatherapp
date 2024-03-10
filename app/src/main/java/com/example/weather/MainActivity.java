package com.example.weather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity {
    private EditText enterField;
    private Button button;
    private TextView resultField;
    private ImageView imageWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//onCreate вызывается в момент создания окна приложения
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        enterField = findViewById(R.id.enterField);
        button = findViewById(R.id.button);
        resultField = findViewById(R.id.resultField);
        imageWeather = findViewById(R.id.imageView);

      button.setOnClickListener(new View.OnClickListener() {//создали слушатель события
          @Override
          public void onClick(View v) {//функция, которую вызывает слушатель
              if (enterField.getText().toString().trim().equals(""))//получаем текст с поля, удаляем пробелы, проверяем не пустая ли строка
            Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();//класс Toast для вызова всплывабщего окна, окно будет показано в классе MainActivity
              else {
                  String city = enterField.getText().toString();
                  String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+",&appid=9cbbdbaf4f0aa3a2b5558f122d628b22&units=metric&lang=ru";
                  new getURLData().execute(url);//cоздаем новый объект нашего класса, вызываем унаследованный метод execute()
        }
          }
      });
        }

        private class getURLData extends AsyncTask<String, String, String>{//вложенный класс для отправки запроса по определенному url адресу, наследуемся от асинхронного класса(асинхронное подключение по определенному URL адресу)

           protected void onPreExecute(){//начало отправки данных по URL адресу, показываем пользователю, что данные были успешно отпрпвлены
                super.onPreExecute();//обращаемся к такому же методу, только в классе AsyncTask через слово super
               //грубо говоря тырим все методы из родительского класса для асинхрорнных задач
               resultField.setText("Запрос отправлен");

           }
            @Override
            protected String doInBackground(String... strings) { //метод выполняет фоновую работу и возвращает вменяемую строку(почти)
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(strings[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuilder dataBuffer = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null)
                        dataBuffer.append(line).append("\n");

                    return dataBuffer.toString();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null)
                        connection.disconnect();

                    try {
                        if (reader != null)
                            reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        double currentTemp = jsonObject.getJSONObject("main").getDouble("temp");
                        double maxTemp = currentTemp + 2;
                        double minTemp = currentTemp - 2;

                        // Используем getJSONArray вместо getJSONObject, потому что дебилизм
                        JSONArray weatherArray = jsonObject.getJSONArray("weather");

                        // Получаем первый объект из массива (если он существует)
                        if (weatherArray.length() > 0) {
                            // Получаем объект из массива
                            JSONObject weatherObject = weatherArray.getJSONObject(0);

                            // Извлекаем значение ключа "description"
                            String description = weatherObject.getString("description");
                            DecimalFormat decimalFormat = new DecimalFormat("#.##");


                            String formattedCurrentTemp = decimalFormat.format(currentTemp);
                            String formattedMaxTemp = decimalFormat.format(maxTemp);
                            String formattedMinTemp = decimalFormat.format(minTemp);
                            String resultText = "Температура: " + formattedCurrentTemp + "\n" +
                                    "Макс: " + formattedMaxTemp + "\n" +
                                    "Мин: " + formattedMinTemp + "\n" +
                                    "Описание: " + description;

                            resultField.setText(resultText);
                            if ("ясно".equals(description)) {
                                // Установите фон для ясной погоды
                                imageWeather.setBackgroundResource(R.drawable.sunny);
                            } else if ("пасмурно".equals(description)) {
                                // Установите фон для пасмурной погоды
                                imageWeather.setBackgroundResource(R.drawable.cloudy);
                            } else if ("небольшой снег".equals(description)) {
                                imageWeather.setBackgroundResource(R.drawable.snow);
                            }else if ("небольшой дождь".equals(description)){
                                    imageWeather.setBackgroundResource(R.drawable.snow);
                            }else if ("переменная облачность".equals(description)){
                                imageWeather.setBackgroundResource(R.drawable.cloudy);
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    resultField.setText("Ошибка при получении данных");
                }
            }

        }
}
