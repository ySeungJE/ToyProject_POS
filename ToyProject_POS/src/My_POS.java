import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import org.apache.commons.lang3.math.NumberUtils;

class Product_Manage {
	private String product_name;
	private int price;
	private int stock;

	public Product_Manage() {
		product_name = "�ʱ�ȭ";
		price = 0;
		stock = -1;
	}

	public Product_Manage(String product_name, int price, int stock) {
		this.product_name = product_name;
		this.price = price;
		this.stock = stock;
	}

	public Product_Manage(String product_name, int price) {
		this.product_name = product_name;
		this.price = price;
		stock = 0;
	}

	void set_name(String s) {
		product_name = s;
	}

	String get_name() {
		return product_name;
	}

	void set_stock(int n) {
		stock = n;
	}

	int get_price() {
		return price;
	}

	void set_price(int p) {
		price = p;
	}

	int get_stock() {
		return stock;
	}
}

class Sold_Product_Manage extends Product_Manage {
	private LocalDateTime dt;
	private int sales_volume;
	private boolean canceled;
	private boolean card_pay;

	public Sold_Product_Manage() {
		super();
		sales_volume = 0;
		dt = null;
		canceled = false;
		card_pay = false;
	}

	public Sold_Product_Manage(String product_name, int price, int sales_volume, LocalDateTime dt, boolean card_pay) {
		super(product_name, price);
		this.dt = dt;
		this.sales_volume = sales_volume;
		this.card_pay = card_pay;
		canceled = false;
	}

	int get_sales_volume() {
		return sales_volume;
	}

	void set_canceled(boolean canceled) {
		this.canceled = canceled;
	}

	boolean get_canceled() {
		return canceled;
	}

	boolean get_card_pay() {
		return card_pay;
	}

	void set_card_pay(boolean card_pay) {
		this.card_pay = card_pay;
	}

	@Override
	public String toString() {
		if (canceled == false) {
			if (card_pay == false)
				return get_name() + " / �Ǹż��� : " + sales_volume + " / " + "�ݾ� : " + get_price() * get_sales_volume()
						+ " <- paid in cash" + '\n' + dt + '\n';
			else
				return get_name() + " / �Ǹż��� : " + sales_volume + " / " + "�ݾ� : " + get_price() * get_sales_volume()
						+ " <- paid with card" + '\n' + dt + '\n';
		} else {
			if (card_pay == false)
				return get_name() + " / �Ǹż��� : " + sales_volume + " / " + "�ݾ� : " + get_price() * get_sales_volume()
						+ " <- paid in cash" + '\n' + dt + " [canceled]" + '\n';
			else
				return get_name() + " / �Ǹż��� : " + sales_volume + " / " + "�ݾ� : " + get_price() * get_sales_volume()
						+ " <- paid with card" + '\n' + dt + " [canceled]" + '\n';
		}
	}
}

class POS_Working {
	private List<Product_Manage> pd_List;
	private List<Sold_Product_Manage> sales_List = new ArrayList<>();
	private int cash;
	private int turnover;
	private Path fp;
	private final String SUPERVISOR_PIN = "981006";
	private final String STAFF_PIN = "0000";
	private int wrong_count = 0;
	private boolean supervisor_mode;

	LocalDateTime dt = LocalDateTime.now();
	Scanner sc = new Scanner(System.in);

	BiFunction<List<Product_Manage>, String, Product_Manage> get_product = (l, s) -> {
		Product_Manage pmm = new Product_Manage();
		for (Product_Manage p : l) {
			if (p.get_name().equals(s)) {
				pmm = p;
			}
		}
		return pmm;
	};

	BiPredicate<List<Product_Manage>, String> check_product = (l, s) -> {
		int check = 0;
		for (Product_Manage p : l) {
			if (p.get_name().equals(s)) {
				check++;
			}
		}
		if (check == 0)
			return false;
		else
			return true;
	};

	public POS_Working() throws IOException {
		turnover = 0;
		cash = 100000;
		pd_List = Arrays.asList(new Product_Manage("���", 6500, 5), new Product_Manage("ũ����", 1300, 10),
				new Product_Manage("����", 1500, 7), new Product_Manage("��10kg", 30000, 3),
				new Product_Manage("�����", 4000, 8), new Product_Manage("���", 1500, 12),
				new Product_Manage("�������", 200, 11), new Product_Manage("���̽�ũ��", 2000, 15));
		pd_List = new ArrayList<>(pd_List);
		sales_List.add(new Sold_Product_Manage());

		Path now_fp = Paths.get("");
		fp = Paths.get(
				now_fp.toAbsolutePath().toString() + "\\" + dt.getMonthValue() + "�� " + dt.getDayOfMonth() + "���� ������");
		fp = Files.createDirectories(fp);
	}

	List<Product_Manage> get_List() {
		return pd_List;
	}

	void showManual() {
		if (supervisor_mode == true)
			System.out.println("\n" + "User : Supervisor" + '\n' + "=============POS �޴���=============" + "\n\n"
					+ "1.��ǰ �Ǹ� | 2.��ǰ ���� | 3.��ǰ �԰�" + '\n' + "4.��ǰ �߰� | 5.���� ��ȸ | 6.���� �� ��� ����" + '\n' + "7.��� ����"
					+ '\n' + '\n' + "-1. program EXIT...." + '\n' + '\n' + " ���� �ܰ� : " + cash + ", ����� : " + turnover
					+ '\n' + "==================================");
		else
			System.out.println("\n" + "User : Staff" + '\n' + "=============POS �޴���=============" + "\n\n"
					+ "1.��ǰ �Ǹ� | 2.��ǰ ���� | 3.��ǰ �԰�" + '\n' + "4.��ǰ �߰� | 5.���� ��ȸ | 6.���� �� ��� ����" + '\n' + "7.��� ����"
					+ "\n\n" + "-1. program EXIT...." + '\n' + '\n' + " ���� �ܰ� : " + cash + ", ����� : " + turnover + '\n'
					+ "==================================");
	}

	void saling() {
		String name;
		boolean right_name;
		int sales_volume = 0;
		int payment = 0;
		int received = 0;
		Product_Manage sold;

		System.out.println();
		pd_List.forEach(p -> System.out
				.println("# " + p.get_name() + " - " + "���� : " + p.get_price() + " / ��� : " + p.get_stock()));

		do {
			System.out.print("\n" + "�ǸŻ�ǰ�� �Է� : ");
			name = sc.nextLine();
			right_name = check_product.test(pd_List, name);
			if (right_name == false)
				System.out.println(name + " ��/�� �߸��� ǰ���Դϴ�. �ٽ� �Է����ֽʽÿ�.");
			else if (get_product.apply(pd_List, name).get_stock() == 0)
				System.out.print(name + " ��/�� ��� ������ ��ǰ�Դϴ�. �ٽ� �Է����ֽʽÿ�. ");
		} while (right_name == false || get_product.apply(pd_List, name).get_stock() == 0);

		sold = get_product.apply(pd_List, name);

		do {
			System.out.print('\n' + sold.get_name() + "�� �Ǹ� ������ �Է��Ͻÿ� : ");
			sales_volume = NumberUtils.toInt(sc.nextLine());
			if (sales_volume <= 0 || sales_volume > sold.get_stock()) {
				System.out.println("0 �̻��� ������ ��� �°� �Է����ֽʽÿ�.");
			}
		} while (sales_volume <= 0 || sales_volume > sold.get_stock());

		do {
			System.out.print('\n' + "1.���� ����" + '\n' + "2.ī�� ����" + '\n' + "-> ");
			payment = NumberUtils.toInt(sc.nextLine());
			if (payment != 1 && payment != 2) {
				System.out.println("������ ���� ��ȣ�� ������ �Է����ֽʽÿ�.");
			}
		} while (payment != 1 && payment != 2);

		if (payment == 1) {
			do {
				System.out.print("���� ������ �ݾ� : ");
				received = NumberUtils.toInt(sc.nextLine());
				if (received < sold.get_price() * sales_volume) {
					System.out.println("�ǸŰ��ݺ��� ū ������ �Է����ֽʽÿ�.");
				}
			} while (received < sold.get_price() * sales_volume);

			System.out.println(received + "���� �ް� " + (received - sold.get_price() * sales_volume) + "���� �Ž����־����ϴ�.");
			cash += sold.get_price() * sales_volume;
			sold.set_stock(sold.get_stock() - sales_volume);
			turnover += sold.get_price() * sales_volume;
			fill_sales_List(sold, sales_volume, false);
		} else {
			System.out.println(sold.get_price() * sales_volume + "���� ī��� �����Ͽ����ϴ�.");
			sold.set_stock(sold.get_stock() - sales_volume);
			turnover += sold.get_price() * sales_volume;
			fill_sales_List(sold, sales_volume, true);
		}
	}

	void product_delete() {
		String name;
		Product_Manage pm = new Product_Manage();
		System.out.print('\n' + "����Ʈ���� ������ ��ǰ�� �Է� : ");
		name = sc.nextLine();
		while (check_product.test(pd_List, name) == false) {
			System.out.print(name + "�� �߸��� ǰ���Դϴ�. �ٽ� �Է����ֽʽÿ� : ");
			name = sc.nextLine();
		}
		pm = get_product.apply(pd_List, name);
		pd_List.remove(pm);
		System.out.println(name + " ��/�� ��ǰ����Ʈ���� ���ŵǾ����ϴ�.");
	}

	void product_adding() {
		String name;
		int price = 0;
		int stock = 0;

		System.out.print('\n' + "�߰��� ��ǰ�� �̸��� �Է��Ͻÿ� : ");
		name = sc.nextLine();
		do {
			System.out.print(name + "�� ������ �Է��Ͻÿ� : ");
			price = NumberUtils.toInt(sc.nextLine());
			if (price == 0) {
				System.out.println("��ǰ�� ������ ������ �Է����ֽʽÿ�.");
			}
		} while (price <= 0);
		do {
			System.out.print(name + "�� ��� �Է��Ͻÿ� : ");
			stock = NumberUtils.toInt(sc.nextLine());
			if (stock == 0) {
				System.out.println("1 �̻��� ����� ������ �Է����ֽʽÿ�.");
			}
		} while (stock <= 0);
		pd_List.add(new Product_Manage(name, price, stock));
		System.out.println('\n' + name + " ��/�� ��ǰ��Ͽ� �߰��Ǿ����ϴ�.");
	}

	void stocking() {
		String name;
		int num;
		Product_Manage p;

		do {
			System.out.print('\n' + "�԰��� ��ǰ���� �Է��Ͻÿ� : ");
			name = sc.nextLine();
			if (check_product.test(pd_List, name) == false)
				System.out.print(name + "�� �߸��� ǰ���Դϴ�. �ٽ� �Է����ֽʽÿ�.");
		} while (check_product.test(pd_List, name) == false);

		do {
			System.out.print("�԰����� �Է��Ͻÿ� : ");
			num = NumberUtils.toInt(sc.nextLine());
			if (num <= 0) {
				System.out.println("1 �̻��� �԰����� ������ �Է����ֽʽÿ�.");
			}
		} while (num <= 0);
		p = get_product.apply(pd_List, name);
		p.set_stock(p.get_stock() + num);
	}

	void fill_sales_List(Product_Manage sold, int sales_volume, boolean card_pay) {
		dt = LocalDateTime.now();
		sales_List.add(new Sold_Product_Manage(sold.get_name(), sold.get_price(), sales_volume, dt, card_pay));
	}

	void sales_check_And_cancel() {
		int choice;
		Sold_Product_Manage sp;
		Product_Manage p;

		if (sales_List.size() == 1) {
			System.out.println('\n' + "�������� �����ϴ�.");
			return;
		} else
			sales_List.forEach(s -> {
				if (s.get_name() == "�ʱ�ȭ")
					System.out.print("");
				else
					System.out.print("\n" + sales_List.indexOf(s) + ". " + s);
			});
		do {
			System.out.print('\n' + "1.���ư���" + '\n' + "2.���� ���" + '\n' + "-> ");
			choice = NumberUtils.toInt(sc.nextLine());
			if (choice != 1 && choice != 2) {
				System.out.println("������ ���� ��ȣ�� ������ �Է����ֽʽÿ�.");
			}
		} while (choice != 1 && choice != 2);

		if (choice == 1)
			return;
		do {
			System.out.print('\n' + "���� ��Ҹ� ���ϴ� �Ǹű���� ��ȣ�� �Է��Ͻÿ� : ");
			choice = NumberUtils.toInt(sc.nextLine());
			if (choice >= sales_List.size() || choice <= 0) {
				System.out.println("������ ���� ��ȣ�� ������ �Է����ֽʽÿ�.");
			}
		} while (choice >= sales_List.size() || choice <= 0);

		sp = sales_List.get(choice);
		sp.set_canceled(true);
		p = get_product.apply(pd_List, sp.get_name());

		if (sp.get_card_pay() == true) {
			System.out.println("[ " + sp.get_name() + ' ' + sp.get_sales_volume() + "�� ] ���� ��ҵ� / "
					+ sp.get_price() * sp.get_sales_volume() + "���� ���� ī��� ȯ���Ͽ����ϴ�.");
			turnover -= sp.get_price() * sp.get_sales_volume();
			p.set_stock(p.get_stock() + sp.get_sales_volume());
		} else {
			System.out.println("[ " + sp.get_name() + ' ' + sp.get_sales_volume() + "�� ] ���� ��ҵ� / "
					+ sp.get_price() * sp.get_sales_volume() + "���� �������� ȯ���Ͽ����ϴ�.");
			turnover -= sp.get_price() * sp.get_sales_volume();
			cash -= sp.get_price() * sp.get_sales_volume();
			p.set_stock(p.get_stock() + sp.get_sales_volume());
		}
	}

	void closing_And_saveRecord() throws IOException {
		dt = LocalDateTime.now();

		Path txt_p = Paths.get(
				fp.toString() + "\\" + dt.getHour() + "��" + dt.getMinute() + "��" + dt.getSecond() + "��_���� ���" + ".txt");
		txt_p = Files.createFile(txt_p);

		List<String> S_List = sales_List.stream().map(s -> s.toString()).filter(s -> s.contains("�ʱ�ȭ") == false)
				.collect(() -> new ArrayList<>(), (c, s) -> c.add(s), (lst1, lst2) -> lst1.addAll(lst2));
		S_List.add("[�ܱ� : " + cash + "��, �� ���� : " + turnover + "��]");

		Files.write(txt_p, S_List);
		System.out.println('\n' + "���� ��� ���� �� �����Ϸ�" + "\n\n" + "POS�� �����մϴ�.");
		System.exit(0);
	}

	boolean check_Othority(String PIN) {
		if (PIN.equals(SUPERVISOR_PIN)) {
			System.out.println("Log_in with Supervisor_mode");
			supervisor_mode = true;
			return true;
		} else if (PIN.equals(STAFF_PIN)) {
			System.out.println("Log_in with Staff_mode");
			supervisor_mode = false;
			return true;
		} else {
			if (++wrong_count == 5) {
				System.out.println('\n' + "��й�ȣ�� 5ȸ �߸� �Է��Ͽ����ϴ�" + '\n' + "�ý����� �����մϴ�.");
				System.exit(0);
			}
			System.out.println("��й�ȣ�� �߸� �ԷµǾ����ϴ�." + '\n' + "5ȸ �߸� �Է� �� �ý����� ����˴ϴ� / Ʋ�� Ƚ�� -> " + wrong_count);
			return false;
		}
	}

	void change_Othority() {
		String PIN;
		if (supervisor_mode) {
			System.out.println('\n' + "������ ���� �����մϴ�.");
			supervisor_mode = false;
		} else {
			do {
				System.out.print('\n' + "������ ��� ��й�ȣ �Է� : ");
				PIN = sc.nextLine();
				if (wrong_count == 5) {
					System.out.println('\n' + "��й�ȣ�� 5ȸ �߸� �Է��Ͽ����ϴ�" + '\n' + "�ý����� �����մϴ�.");
					System.exit(0);
				} else if (PIN.equals(SUPERVISOR_PIN) == false) {
					System.out
							.println("��й�ȣ�� �߸� �ԷµǾ����ϴ�." + '\n' + "5ȸ �߸� �Է� �� �ý����� ����˴ϴ� / Ʋ�� Ƚ�� -> " + ++wrong_count);
				}
			} while (PIN.equals(SUPERVISOR_PIN) == false);
			System.out.println('\n' + "������ ���� �����մϴ�.");
			supervisor_mode = true;
		}
	}

	void POS_ON() throws IOException {
		int choice;
		String PIN;
		System.out.println("My_POS�� �����մϴ�" + "\n\n" + "������ ��� ��й�ȣ -> 981006" + '\n' + "������ ��� ��й�ȣ -> 0000");
		do {
			System.out.print('\n' + "��й�ȣ �Է� : ");
			PIN = sc.nextLine();
		} while (check_Othority(PIN) == false);
		wrong_count = 0;

		while (true) {
			showManual();
			do {
				System.out.print("���� > ");
				choice = NumberUtils.toInt(sc.nextLine());
				if (choice < -1 || choice == 0 || choice > 7) {
					System.out.println("������ ���� ��ȣ�� ������ �Է����ֽʽÿ�.");
				}
			} while (choice < -1 || choice == 0 || choice > 7);

			switch (choice) {
			case 1:
				saling();
				break;
			case 2:
				if (supervisor_mode == false) {
					System.out.println('\n' + "'��ǰ ����'�� ������ ��忡�� ��� ������ ����Դϴ�.");
					break;
				}
				product_delete();
				break;
			case 3:
				if (supervisor_mode == false) {
					System.out.println('\n' + "'��ǰ �԰�'�� ������ ��忡�� ��� ������ ����Դϴ�.");
					break;
				}
				stocking();
				break;
			case 4:
				if (supervisor_mode == false) {
					System.out.println('\n' + "'��ǰ �߰�'�� ������ ��忡�� ��� ������ ����Դϴ�.");
					break;
				}
				product_adding();
				break;
			case 5:
				sales_check_And_cancel();
				break;
			case 6:
				closing_And_saveRecord();
				break;
			case 7:
				change_Othority();
				break;
			case -1:
				System.out.println('\n' + "�ý����� �����մϴ�.");
				return;
			}
		}
	}
}

public class My_POS {
	public static void main(String[] args) throws IOException {
		POS_Working pw = new POS_Working();
		pw.POS_ON();
	}
}
