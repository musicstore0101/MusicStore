package app.music.musicstore.ui.login;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.net.ssl.HttpsURLConnection;

import app.music.musicstore.GlobalDefinitions;
import app.music.musicstore.data.Result;
import app.music.musicstore.data.model.LoggedInUser;
import app.music.musicstore.ui.login.LoginResult;

import static app.music.musicstore.GlobalDefinitions.g_mohammadRafiDownloadPath;
import static app.music.musicstore.GlobalDefinitions.g_mohammadRafiSongListName;

public class AuthenticateUser extends AsyncTask<String, Void, String> {

    private LoginViewModel loginViewModel;
    AuthenticateUser(LoginViewModel loginViewModel)
    {
        this.loginViewModel = loginViewModel;
    }
    protected String doInBackground(String... urls) {
        String parameters = urls[0];
        try {
            URL url = new URL("http://ec2-13-234-37-59.ap-south-1.compute.amazonaws.com:8085/greeting?name="+parameters);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //handle timeout shantanu

            // Hack to force HttpURLConnection to run the request
            // Otherwise getErrorStream always returns null
            int rsp = connection.getResponseCode();
            String responseBody = connection.getResponseMessage();
            InputStream stream = connection.getErrorStream();
            if (stream == null) {
                stream = connection.getInputStream();
            }

            if(rsp == 200) {
                //convertStreamToString
                responseBody = convertStreamToString(connection.getInputStream());
                //responseBody = readFully(connection.getInputStream()).toString("UTF-8");
                System.out.println("@@@@ Shantanu response code = " + rsp
                        + " response body = " + responseBody);

                responseBody.replace("\n", "");
                responseBody.replace(" \\", "");
                System.out.println("@@@@ Shantanu response code = " + rsp
                        + " \nresponse body = " + responseBody);
            }
            else
            {
                responseBody = "failed";
            }

            return responseBody;
            //return new Result.Success<>(user);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    protected void onPostExecute(String messageBody)
    {

        String username = "";
        String password = "";
        String displayname = "";
        int credit = 0;

        if(messageBody == "failed")
        {
            Exception e = new Exception();
            Result<LoggedInUser> result =  new Result.Error(new Exception("Error logging in"));
            loginViewModel.showLoginResult(result);
            return;
        }
        //shantanu parse json body and create username, displayname and credit

        try {
            // get JSONObject from JSON file
            JSONObject obj1 = new JSONObject(messageBody);
            //JSONObject obj = obj1.getJSONObject("content");
            String s = obj1.getString("content");

            System.out.println("********** shantanu s = \n"+s);
            JSONObject obj = new JSONObject(s);
            String valid = obj.getString("valid");

            if(valid == "false")
            {
                Exception e = new Exception();
                Result<LoggedInUser> result =  new Result.Error(new Exception("Error logging in"));
                loginViewModel.showLoginResult(result);
                return;
            }
            else {
                JSONObject cred = obj.getJSONObject("credentials");

                username = cred.getString("username");
                password = cred.getString("password");
                displayname = cred.getString("displayname");
                credit = cred.getInt("credit");

                System.out.println("########## shantanu = " + username + " " + password +
                        " " + displayname + " " + credit);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LoggedInUser user =
                new LoggedInUser(username,
                        displayname);
        user.setPassword(password);

        Result<LoggedInUser> result = new Result.Success<>(user);
        loginViewModel.showLoginResult(result);
    }

    private ByteArrayOutputStream readFully(InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos;
    }

    private String convertStreamToString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
