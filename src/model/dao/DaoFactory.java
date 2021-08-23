package model.dao;

import db.DB;
import model.dao.impl.VendedorDaoJDBC;

public class DaoFactory {
	
	//e um macete para n�o precisa expor a implementa��o.
	public static VendedorDao criarVendedorDao() {
		return new VendedorDaoJDBC(DB.getConnection());
	}
}
