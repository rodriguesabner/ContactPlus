package com.kingaspx.contatoswhatsapp.Util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.kingaspx.contatoswhatsapp.HomeActivity;
import com.kingaspx.contatoswhatsapp.Model.Contato;
import com.kingaspx.contatoswhatsapp.Model.ContatoComparator;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class RetrieveContacts {
    private HomeActivity mainActivity;
    private List<Contato> contatoList;


    public RetrieveContacts(HomeActivity mainActivity, List<Contato> contatoList) {
        this.mainActivity = mainActivity;
        this.contatoList = contatoList;
    }

    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts._ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    public void getContacts() {
        ContentResolver cr = mainActivity.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
        if (cursor != null) {
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                contatoList.clear();
                while (cursor.moveToNext()) {
                    Contato contato = new Contato();

                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);

                    contato.setName(name);
                    contato.setPhone(number);
                    contatoList.add(contato);
                }

                HashSet<Contato> hashSet = new HashSet<>();
                hashSet.addAll(contatoList);

                contatoList.clear();
                contatoList.addAll(hashSet);
                Collections.sort(contatoList, new ContatoComparator());
            } finally {
                cursor.close();
            }
        }
    }
}
