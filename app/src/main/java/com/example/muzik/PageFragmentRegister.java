package com.example.muzik;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageFragmentRegister extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private EditText RgtName, RgtUsername, RgtPassword, RgtRetypePassword, RgtEmail;
    TextView UsernameMess, PasswordMess, EmailMess, NameMess, RetypePasswordMess;
    Button Submit;
    ViewPager parent;

    public static PageFragmentRegister newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragmentRegister fragment = new PageFragmentRegister();
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
        View view = inflater.inflate(R.layout.register_layout, container, false);
        parent = (ViewPager) container;
        RgtName = view.findViewById(R.id.ET_Rgt_Name);
        RgtUsername = view.findViewById(R.id.ET_Rgt_Username);
        RgtPassword = view.findViewById(R.id.ET_Rgt_Password);
        RgtRetypePassword = view.findViewById(R.id.ET_Rgt_RetypePassword);
        RgtEmail = view.findViewById(R.id.ET_Rgt_Email);

        UsernameMess = view.findViewById(R.id.TV_Rgt_Username);
        PasswordMess = view.findViewById(R.id.TV_Rgt_Password);
        EmailMess = view.findViewById(R.id.TV_Rgt_Email);
        NameMess = view.findViewById(R.id.TV_Rgt_Name);
        ;
        RetypePasswordMess = view.findViewById(R.id.TV_Rgt_RetypePassword);

        Submit = view.findViewById(R.id.Btn_Rgt_Register);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validate();
                } catch (JSONException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        return view;
    }

    public void validate() throws JSONException, InterruptedException {
        String pass = RgtPassword.getText().toString();
        String username = RgtUsername.getText().toString();
        String email = RgtEmail.getText().toString();
        String retypePass = RgtRetypePassword.getText().toString();
        String name = RgtName.getText().toString();
        boolean flag = true;
        flag = flag && validateUsername(username);
        flag = flag && validatePassword(pass) && validateRetypePass(retypePass);
        flag = flag && validateEmail(email);
        AtomicReference<String> response = new AtomicReference<>("");
        if (flag) {
            Thread t = new Thread(() -> {
                try {
                    response.set(HttpRequest.register(username, pass, email, name));
                    Log.e("TAG", response.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t.start();
            t.join();


            JSONObject obj = new JSONObject(response.toString());
            if (obj.getString("status").equals("false")) {
                if (obj.getString("type").equals("username")) {
                    UsernameMess.setText("Tên đăng nhập đã tồn tại");
                    RgtUsername.requestFocus();
                } else if (obj.getString("type").equals("email")) {
                    EmailMess.setText("Email đã tồn tại");
                    RgtEmail.requestFocus();
                }
            } else if (obj.getString("status").equals("true")) {
                ((EditText)getActivity().findViewById(R.id.ET_Lg_Username)).setText(RgtUsername.getText().toString());
                RgtName.setText("");
                RgtUsername.setText("");
                RgtPassword.setText("");
                RgtRetypePassword.setText("");
                RgtEmail.setText("");
                Toast.makeText(getContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                ((ViewPager) getActivity().findViewById(R.id.ViewPagerLogin)).setCurrentItem(0, true);
                ((EditText)getActivity().findViewById(R.id.ET_Lg_Password)).requestFocus();
            }
        }
    }


    public Boolean validatePassword(String s) {
        Pattern notContainSpecialChar = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher specialChar = notContainSpecialChar.matcher(s);
        boolean S = specialChar.find();
        if (s.length() < 8 || s.length() > 16) {
            PasswordMess.setText("Mật khẩu phải chứa từ 8-16 ký tự");
            RgtPassword.requestFocus();
            return false;
        } else if (S) {
            PasswordMess.setText("Mật khẩu không được chứa kí tự đặc biệt");
            RgtPassword.requestFocus();
            return false;
        } else if (!containsDigit(s)) {
            PasswordMess.setText("Mật khẩu phải chứa ít nhất một số");
            RgtPassword.requestFocus();
            return false;
        } else if (!containsUpercase(s)) {
            PasswordMess.setText("Mật khẩu phải chứa một ký tự in hoa");
            RgtPassword.requestFocus();
            return false;
        }
        PasswordMess.setText("");
        return true;
    }

    public boolean validateUsername(String s) {
        Pattern notContainSpecialChar = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher specialChar = notContainSpecialChar.matcher(s);
        boolean S = specialChar.find();
        if (S) {
            UsernameMess.setText("Tên đăng nhập không được chứa kí tự đặc biệt");
            RgtUsername.requestFocus();
            return false;
        }
        UsernameMess.setText("");
        return true;
    }

    public boolean validateEmail(String s) {
        if (!s.contains("@") || !s.contains(".com")) {
            EmailMess.setText("Email không hợp lệ");
            RgtEmail.requestFocus();
            return false;
        }
        EmailMess.setText("");
        return true;
    }

    public boolean validateRetypePass(String s) {
        if (!s.equals(RgtPassword.getText().toString())) {
            RetypePasswordMess.setText("Password không đúng");
            RgtRetypePassword.requestFocus();
            return false;
        }
        RetypePasswordMess.setText("");
        return true;
    }

    public final boolean containsDigit(String s) {
        boolean containsDigit = false;

        if (s != null && !s.isEmpty()) {
            for (char c : s.toCharArray()) {
                if (containsDigit = Character.isDigit(c)) {
                    break;
                }
            }
        }

        return containsDigit;
    }

    public final boolean containsUpercase(String s) {
        boolean containsUpercase = false;

        if (s != null && !s.isEmpty()) {
            for (char c : s.toCharArray()) {
                if (containsUpercase = Character.isUpperCase(c)) {
                    break;
                }
            }
        }
        return containsUpercase;
    }
}