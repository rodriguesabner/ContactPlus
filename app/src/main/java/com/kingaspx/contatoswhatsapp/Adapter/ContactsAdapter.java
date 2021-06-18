package com.kingaspx.contatoswhatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kingaspx.contatoswhatsapp.Model.Contato;
import com.kingaspx.contatoswhatsapp.R;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    List<Contato> contatoList;
    Context context;

    public ContactsAdapter(Context context, List<Contato> contatoList) {
        this.contatoList = contatoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.rowcontact, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, int position) {
        Contato contato = contatoList.get(position);

        holder.linearLayout.setOnClickListener((action) -> {
            Intent intent = new Intent(
                    ContactsContract.Intents.SHOW_OR_CREATE_CONTACT,
                    ContactsContract.Contacts.CONTENT_URI);
            intent.setData(Uri.parse("tel:" + contato.getPhone()));
            context.startActivity(intent);
        });

        holder.name.setText(contato.getName());
        holder.phone.setText(contato.getPhone());
    }

    @Override
    public int getItemCount() {
        return contatoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView name, phone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linear_contact_main);
            name = itemView.findViewById(R.id.ctt_name);
            phone = itemView.findViewById(R.id.ctt_phone);
        }

    }
}