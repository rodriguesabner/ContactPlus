package com.kingaspx.contatoswhatsapp.Util;

import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.WorkerThread;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kingaspx.contatoswhatsapp.HomeActivity;
import com.kingaspx.contatoswhatsapp.Model.Contato;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;

import static android.content.Context.MODE_PRIVATE;

public class SocketUtil {
    private SharedPreferences sharedId;
    private final List<Contato> contatoList;
    private HomeActivity mainActivity;

    private final InsertContact insertContact;
    private final Functions functions;
    private Socket socket;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("users");

    public SocketUtil(HomeActivity mainActivity, List<Contato> contatoList, SharedPreferences sharedPreferences) {
        this.contatoList = contatoList;
        this.mainActivity = mainActivity;

        functions = new Functions(sharedPreferences);
        insertContact = new InsertContact(mainActivity);

        URI uri = URI.create(functions.baseUrlSocketIo());
        IO.Options options = IO.Options.builder().build();

        socket = IO.socket(uri, options);
    }

    public void getSocket() {
        sharedId = mainActivity.getSharedPreferences("kingaspx-login", MODE_PRIVATE);

        try {
            socket.on("client_first_access", this::storeSocketId)
                    .on("new-contact", this::createContacts)
                    .on("get-contacts", this::listContacts);

            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @WorkerThread
    void createContacts(Object[] args) {
        mainActivity.runOnUiThread(() -> {
            try {
                JSONObject jsonObject = (JSONObject) args[0];
                String APP_ID = (String) jsonObject.get("APP_ID");
                String userId = sharedId.getString("id-main", null);

                if (APP_ID.equals(userId)) {
                    JSONArray jsonArray = (JSONArray) jsonObject.get("contacts");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = (JSONObject) jsonArray.get(i);

                        String name = (String) item.get("name");
                        String phone = (String) item.get("phone");

                        insertContact.insertContact(name, phone);
                        Toast.makeText(mainActivity, "Contact " + name + " Added", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @WorkerThread
    void listContacts(Object[] args) {
        mainActivity.runOnUiThread(() -> {
            JSONArray jsonArray = new JSONArray();

            try {
                JSONObject argsApp = (JSONObject) args[0];
                String APP_ID = (String) argsApp.get("APP_ID");
                String userId = sharedId.getString("id-main", null);
                System.out.println(userId);

                if (APP_ID.equals(userId)) {
                    JSONObject obj = new JSONObject();
                    obj.put("APP_ID", APP_ID);
                    jsonArray.put(obj);

                    HashSet<Contato> hashSet = new HashSet<>();
                    hashSet.addAll(contatoList);
                    contatoList.clear();
                    contatoList.addAll(hashSet);

                    for (Contato contato : contatoList) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name", contato.getName());
                        jsonObject.put("phone", contato.getPhone());
                        jsonArray.put(jsonObject);
                    }

                    Toast.makeText(mainActivity, "Sending Contacts", Toast.LENGTH_SHORT).show();
                    socket.emit("list-contacts", jsonArray);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @WorkerThread
    void storeSocketId(Object[] args) {
        try {
            JSONObject jsonObject = (JSONObject) args[0];
            String socket_id = (String) jsonObject.get("socket_id");

            Map<String, Object> map = new HashMap<>();
            map.put("socket_id", socket_id);

            String userId = sharedId.getString("id-main", null);
            usersRef.child(userId).updateChildren(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}