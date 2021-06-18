package com.kingaspx.contatoswhatsapp.Model;

import java.util.Comparator;

public class ContatoComparator implements Comparator<Contato> {
    public int compare(Contato o1, Contato o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
