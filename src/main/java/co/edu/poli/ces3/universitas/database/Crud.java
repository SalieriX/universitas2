package co.edu.poli.ces3.universitas.database;

import co.edu.poli.ces3.universitas.dao.User;

import java.util.List;

public interface Crud {
    public void insert(User x);

    public void update(User x);

    public List<User> get();
}
