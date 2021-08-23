package model.dao;

import db.DB;
import model.dao.impl.VendedorDaoJDBC;

public class DaoFactory {
	
	//e um macete para não precisa expor a implementação.
	public static VendedorDao criarVendedorDao() {
		return new VendedorDaoJDBC(DB.getConnection());
	}
}
