package application;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

import model.dao.DaoFactory;
import model.dao.VendedorDao;
import model.entities.Departamento;
import model.entities.Vendedor;

public class Programa {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		//Com isso o programa não conhece a implementação somente a interface.
		//E uma forma de fazer uma injeção de dependecia sem explicitação da implementação.
		VendedorDao vendedorDao = DaoFactory.criarVendedorDao();
		
		System.out.println("==== TEST: 1 findById ====");
		Vendedor vendedor = vendedorDao.findById(3);
		System.out.println(vendedor);
		
		System.out.println("\n ==== TEST: 2 findByDepartamento ====");
		Departamento departamento = new Departamento(2, null);
		List<Vendedor> list = vendedorDao.findDepartament(departamento);
		
		for(Vendedor obj : list) {
			System.out.println(obj);
		}
		
		System.out.println("\n ==== TEST: 3 findAll ====");
		list = vendedorDao.findAll();
		
		for(Vendedor obj : list) {
			System.out.println(obj);
		}
		
		System.out.println("\n ==== TEST: 4 insert ====");
		Vendedor newVendedor = new Vendedor(null, "Angel", "angel@gmail.com", new Date(), 4000.0, departamento);
		vendedorDao.insert(newVendedor);
		System.out.println("Inserindo novo id: " + newVendedor.getId());
		
		System.out.println("\n ==== TEST: 5 update ====");
		vendedor = vendedorDao.findById(1);
		vendedor.setNome("Bruna"); 
		vendedorDao.update(vendedor);
		System.out.println("Update completo");
		
		System.out.println("\n ==== TEST: 6 delete ====");
		System.out.print("Digite o id a ser excluido:");
		int idDelete = sc.nextInt();
		vendedorDao.delete(idDelete);
		System.out.println("Deletado com sucesso!");
				
		sc.close();
	}
}
