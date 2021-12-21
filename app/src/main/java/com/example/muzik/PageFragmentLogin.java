package com.example.muzik;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static android.app.Activity.RESULT_OK;

public class PageFragmentLogin extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    EditText Password, Username;
    TextView Message;
    Button Submit;

    public static PageFragmentLogin newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragmentLogin fragment = new PageFragmentLogin();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_layout, container, false);

        Username = view.findViewById(R.id.ET_Lg_Username);
        Password = view.findViewById(R.id.ET_Lg_Password);

        Message = view.findViewById(R.id.Message);

        Submit = view.findViewById(R.id.Btn_Lg_Login);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validate();
                } catch (InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    public void validate() throws InterruptedException, JSONException {
        String username = Username.getText().toString();
        String password = Password.getText().toString();
        AtomicReference<String> response = new AtomicReference<>("");
        Thread t = new Thread(() -> {
            try {
                response.set(HttpRequest.login(username, password));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                getActivity().runOnUiThread(()->{
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response.toString());
                        if (obj.getString("status").equals("true")) {
                            saveData(new JSONObject(obj.getString("data")));
                            Toast.makeText(getContext(), "Bạn đã đăng nhâp thành công", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getContext(), MainActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        } else {
                            Message.setText("Sai tên đăng nhập hoặc mật khẩu");
                        }
                    } catch (JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        t.start();


    }

    public void saveData(JSONObject obj) throws JSONException, InterruptedException {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SESSION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", obj.getString("TenUser"));
        editor.putString("email", obj.getString("Email"));
        editor.putString("userid", obj.getString("UserID"));
        editor.putString("avatar", getAva(obj.getString("UserID")));
        editor.apply();
    }

    public String getAva(String userId) throws InterruptedException {
        AtomicReference<String> result = new AtomicReference<>("");
        Thread t = new Thread(() -> {
            try {
                result.set(BitMapToString(HttpRequest.getAva(userId)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
        return result.toString();
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


}