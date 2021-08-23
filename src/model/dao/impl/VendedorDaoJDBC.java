package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.VendedorDao;
import model.entities.Departamento;
import model.entities.Vendedor;

public class VendedorDaoJDBC implements VendedorDao{
	
	private Connection connection;
	
	//Aqui esta forçando uma injeção.
	public VendedorDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void insert(Vendedor obj) {
		
		PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, obj.getNome());
			ps.setString(2, obj.getEmail());
			ps.setDate(3, new java.sql.Date(obj.getDataAniversario().getTime()));
			ps.setDouble(4, obj.getSalarioBase());
			ps.setInt(5, obj.getDepartamento().getId());
			
			int LinhaAfetadas = ps.executeUpdate();
			
			if(LinhaAfetadas > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				else {
					throw new DbException("Erro inesperado! Nenhuma linha foi afetada.");
				}
				DB.closeResultSet(rs);
			}
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public void update(Vendedor obj) {
	PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ?");
			
			ps.setString(1, obj.getNome());
			ps.setString(2, obj.getEmail());
			ps.setDate(3, new java.sql.Date(obj.getDataAniversario().getTime()));
			ps.setDouble(4, obj.getSalarioBase());
			ps.setInt(5, obj.getDepartamento().getId());
			ps.setInt(6, obj.getId());
			
			ps.executeUpdate();
			
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public void delete(Integer id) {
		PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement("DELETE FROM seller WHERE Id= ?");
			
			ps.setInt(1, id);
			
			ps.executeUpdate();
			
			
		}
		catch(SQLException e) {
		throw new DbException(e.getMessage());
		}
	}

	@Override
	public Vendedor findById(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = connection.prepareStatement(
					"SELECT seller.*, " 
					+ "department.Name AS DepNome " 
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?"
					);
			//id sera usando pq e o que vai receber no parametro.
			ps.setInt(1, id);
			
			rs = ps.executeQuery();
			//esse if e usado devido o ResultSet inicia com o indice 0, e o valor dos Dados começa no indice 1.
			if(rs.next()) {
				Departamento departamento = instanciaDepartamento(rs);
				
				Vendedor vendedor = instanciaVendedor(rs, departamento);
				
				return vendedor;
			}
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
		return null;
	}

	private Departamento instanciaDepartamento(ResultSet rs) throws SQLException {
		Departamento departamento = new Departamento();
		departamento.setId(rs.getInt("Id"));
		departamento.setNome(rs.getString("Name"));
		
		return departamento;
	}
	
	private Vendedor instanciaVendedor(ResultSet rs, Departamento departamento) throws SQLException {
		Vendedor vendedor = new Vendedor();
		vendedor.setId(rs.getInt("Id"));
		vendedor.setNome(rs.getString("Name"));
		vendedor.setEmail(rs.getString("Email"));
		vendedor.setDataAniversario(rs.getDate("BirthDate"));
		vendedor.setSalarioBase(rs.getDouble("BaseSalary"));
		vendedor.setDepartamento(departamento);
		return vendedor;
	}

	@Override
	public List<Vendedor> findAll() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = connection.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name"
					);
			
			rs = ps.executeQuery();
			
			List<Vendedor> list = new ArrayList<>();
			
			Map<Integer, Departamento> listMap = new HashMap<>();
			
			while(rs.next()) {
				//esse será usado para ver se tem no vendedor um id do departamento.
				//se não tiver ele vai instancia, usando a condição abaixo.
				Departamento dep = listMap.get(rs.getInt("DepartmentId"));
				
				if(dep == null) {
					//se caso não tive um DepartmentId então ele vai instância um.
					dep = instanciaDepartamento(rs);
					//assim ele vai atualizar pelo chave DepartmentId e o value e a instância.
					//put ele cria ou substituir.
					listMap.put(rs.getInt("DepartmentId"), dep);
				}
				
				Vendedor vend = instanciaVendedor(rs, dep);
				list.add(vend);
				
			}
			return list;
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}

	}

	@Override
	public List<Vendedor> findDepartament(Departamento departamento) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = connection.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name"
					);
			
			ps.setInt(1, departamento.getId());
			
			rs = ps.executeQuery();
			
			List<Vendedor> list = new ArrayList<>();
			
			Map<Integer, Departamento> listMap = new HashMap<>();
			
			while(rs.next()) {
				//esse será usado para ver se tem no vendedor um id do departamento.
				//se não tiver ele vai instancia, usando a condição abaixo.
				Departamento dep = listMap.get(rs.getInt("DepartmentId"));
				
				if(dep == null) {
					//se caso não tive um DepartmentId então ele vai instância um.
					dep = instanciaDepartamento(rs);
					//assim ele vai atualizar pelo chave DepartmentId e o value e a instância.
					//put ele cria ou substituir.
					listMap.put(rs.getInt("DepartmentId"), dep);
				}
				
				Vendedor vend = instanciaVendedor(rs, dep);
				list.add(vend);
				
			}
			return list;
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}
}
