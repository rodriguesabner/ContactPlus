package com.kingaspx.contatoswhatsapp.Model;

import java.util.Objects;

public class Contato {
    private Integer id;
    private String name;
    private String phone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contato contato = (Contato) o;
        return Objects.equals(name, contato.name) &&
                Objects.equals(phone, contato.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone);
    }
}
