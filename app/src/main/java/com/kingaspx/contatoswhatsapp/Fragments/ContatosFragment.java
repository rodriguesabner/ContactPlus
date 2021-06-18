package com.kingaspx.contatoswhatsapp.Fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kingaspx.contatoswhatsapp.Adapter.ContactsAdapter;
import com.kingaspx.contatoswhatsapp.HomeActivity;
import com.kingaspx.contatoswhatsapp.Model.Contato;
import com.kingaspx.contatoswhatsapp.Model.ContatoComparator;
import com.kingaspx.contatoswhatsapp.R;
import com.kingaspx.contatoswhatsapp.Util.RetrieveContacts;
import com.kingaspx.contatoswhatsapp.Util.SocketUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ContatosFragment extends Fragment {

    public static List<Contato> contatoList;
    private ContactsAdapter adapter;
    private RecyclerView recyclerContatos;
    private LinearLayoutManager mLayoutManager;
    private HomeActivity homeActivity;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    public ContatosFragment() {
    }

    public ContatosFragment(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contatoList = new ArrayList<>();
        checkPermission();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("kingaspx-contacts", MODE_PRIVATE);
        new SocketUtil(homeActivity, contatoList, sharedPreferences).getSocket();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerContatos = view.findViewById(R.id.listctt);

        recyclerContatos.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());

        recyclerContatos.setLayoutManager(mLayoutManager);
        adapter = new ContactsAdapter(homeActivity, contatoList);

        recyclerContatos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contatos, container, false);
    }

    private void checkPermission() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Read Contacts");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_CONTACTS))
            permissionsNeeded.add("Write Contacts");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message, (dialog, which) -> requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS));
                return;
            }

            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

        RetrieveContacts retrieveContacts = new RetrieveContacts(homeActivity, contatoList);
        Collections.sort(contatoList, new ContatoComparator());
        retrieveContacts.getContacts();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (getActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            return shouldShowRequestPermissionRationale(permission);
        }
        return true;
    }

    public void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                RetrieveContacts retrieveContacts = new RetrieveContacts(homeActivity, contatoList);
                retrieveContacts.getContacts();
                adapter.notifyDataSetChanged();
            }

            return;
        }
    }

}