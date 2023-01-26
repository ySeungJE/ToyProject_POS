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
		product_name = "초기화";
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
				return get_name() + " / 판매수량 : " + sales_volume + " / " + "금액 : " + get_price() * get_sales_volume()
						+ " <- paid in cash" + '\n' + dt + '\n';
			else
				return get_name() + " / 판매수량 : " + sales_volume + " / " + "금액 : " + get_price() * get_sales_volume()
						+ " <- paid with card" + '\n' + dt + '\n';
		} else {
			if (card_pay == false)
				return get_name() + " / 판매수량 : " + sales_volume + " / " + "금액 : " + get_price() * get_sales_volume()
						+ " <- paid in cash" + '\n' + dt + " [canceled]" + '\n';
			else
				return get_name() + " / 판매수량 : " + sales_volume + " / " + "금액 : " + get_price() * get_sales_volume()
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
		pd_List = Arrays.asList(new Product_Manage("계란", 6500, 5), new Product_Manage("크림빵", 1300, 10),
				new Product_Manage("과자", 1500, 7), new Product_Manage("쌀10kg", 30000, 3),
				new Product_Manage("양배추", 4000, 8), new Product_Manage("라면", 1500, 12),
				new Product_Manage("막대사탕", 200, 11), new Product_Manage("아이스크림", 2000, 15));
		pd_List = new ArrayList<>(pd_List);
		sales_List.add(new Sold_Product_Manage());

		Path now_fp = Paths.get("");
		fp = Paths.get(
				now_fp.toAbsolutePath().toString() + "\\" + dt.getMonthValue() + "월 " + dt.getDayOfMonth() + "일자 매출기록");
		fp = Files.createDirectories(fp);
	}

	List<Product_Manage> get_List() {
		return pd_List;
	}

	void showManual() {
		if (supervisor_mode == true)
			System.out.println("\n" + "User : Supervisor" + '\n' + "=============POS 메뉴얼=============" + "\n\n"
					+ "1.상품 판매 | 2.상품 제거 | 3.물품 입고" + '\n' + "4.상품 추가 | 5.매출 조회 | 6.마감 후 기록 저장" + '\n' + "7.모드 변경"
					+ '\n' + '\n' + "-1. program EXIT...." + '\n' + '\n' + " 현금 잔고 : " + cash + ", 매출액 : " + turnover
					+ '\n' + "==================================");
		else
			System.out.println("\n" + "User : Staff" + '\n' + "=============POS 메뉴얼=============" + "\n\n"
					+ "1.상품 판매 | 2.상품 제거 | 3.물품 입고" + '\n' + "4.상품 추가 | 5.매출 조회 | 6.마감 후 기록 저장" + '\n' + "7.모드 변경"
					+ "\n\n" + "-1. program EXIT...." + '\n' + '\n' + " 현금 잔고 : " + cash + ", 매출액 : " + turnover + '\n'
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
				.println("# " + p.get_name() + " - " + "가격 : " + p.get_price() + " / 재고 : " + p.get_stock()));

		do {
			System.out.print("\n" + "판매상품명 입력 : ");
			name = sc.nextLine();
			right_name = check_product.test(pd_List, name);
			if (right_name == false)
				System.out.println(name + " 은/는 잘못된 품명입니다. 다시 입력해주십시오.");
			else if (get_product.apply(pd_List, name).get_stock() == 0)
				System.out.print(name + " 은/는 재고 소진된 상품입니다. 다시 입력해주십시오. ");
		} while (right_name == false || get_product.apply(pd_List, name).get_stock() == 0);

		sold = get_product.apply(pd_List, name);

		do {
			System.out.print('\n' + sold.get_name() + "의 판매 개수를 입력하시오 : ");
			sales_volume = NumberUtils.toInt(sc.nextLine());
			if (sales_volume <= 0 || sales_volume > sold.get_stock()) {
				System.out.println("0 이상의 정수를 재고에 맞게 입력해주십시오.");
			}
		} while (sales_volume <= 0 || sales_volume > sold.get_stock());

		do {
			System.out.print('\n' + "1.현금 결제" + '\n' + "2.카드 결제" + '\n' + "-> ");
			payment = NumberUtils.toInt(sc.nextLine());
			if (payment != 1 && payment != 2) {
				System.out.println("선택지 내의 번호를 정수로 입력해주십시오.");
			}
		} while (payment != 1 && payment != 2);

		if (payment == 1) {
			do {
				System.out.print("받은 현금의 금액 : ");
				received = NumberUtils.toInt(sc.nextLine());
				if (received < sold.get_price() * sales_volume) {
					System.out.println("판매가격보다 큰 정수를 입력해주십시오.");
				}
			} while (received < sold.get_price() * sales_volume);

			System.out.println(received + "원을 받고 " + (received - sold.get_price() * sales_volume) + "원을 거슬러주었습니다.");
			cash += sold.get_price() * sales_volume;
			sold.set_stock(sold.get_stock() - sales_volume);
			turnover += sold.get_price() * sales_volume;
			fill_sales_List(sold, sales_volume, false);
		} else {
			System.out.println(sold.get_price() * sales_volume + "원을 카드로 결제하였습니다.");
			sold.set_stock(sold.get_stock() - sales_volume);
			turnover += sold.get_price() * sales_volume;
			fill_sales_List(sold, sales_volume, true);
		}
	}

	void product_delete() {
		String name;
		Product_Manage pm = new Product_Manage();
		System.out.print('\n' + "리스트에서 제거할 상품명 입력 : ");
		name = sc.nextLine();
		while (check_product.test(pd_List, name) == false) {
			System.out.print(name + "은 잘못된 품명입니다. 다시 입력해주십시오 : ");
			name = sc.nextLine();
		}
		pm = get_product.apply(pd_List, name);
		pd_List.remove(pm);
		System.out.println(name + " 이/가 상품리스트에서 제거되었습니다.");
	}

	void product_adding() {
		String name;
		int price = 0;
		int stock = 0;

		System.out.print('\n' + "추가할 상품의 이름을 입력하시오 : ");
		name = sc.nextLine();
		do {
			System.out.print(name + "의 가격을 입력하시오 : ");
			price = NumberUtils.toInt(sc.nextLine());
			if (price == 0) {
				System.out.println("상품의 정상가을 정수로 입력해주십시오.");
			}
		} while (price <= 0);
		do {
			System.out.print(name + "의 재고를 입력하시오 : ");
			stock = NumberUtils.toInt(sc.nextLine());
			if (stock == 0) {
				System.out.println("1 이상의 재고량을 정수로 입력해주십시오.");
			}
		} while (stock <= 0);
		pd_List.add(new Product_Manage(name, price, stock));
		System.out.println('\n' + name + " 이/가 상품목록에 추가되었습니다.");
	}

	void stocking() {
		String name;
		int num;
		Product_Manage p;

		do {
			System.out.print('\n' + "입고할 상품명을 입력하시오 : ");
			name = sc.nextLine();
			if (check_product.test(pd_List, name) == false)
				System.out.print(name + "은 잘못된 품명입니다. 다시 입력해주십시오.");
		} while (check_product.test(pd_List, name) == false);

		do {
			System.out.print("입고개수를 입력하시오 : ");
			num = NumberUtils.toInt(sc.nextLine());
			if (num <= 0) {
				System.out.println("1 이상의 입고물량을 정수로 입력해주십시오.");
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
			System.out.println('\n' + "매출기록이 없습니다.");
			return;
		} else
			sales_List.forEach(s -> {
				if (s.get_name() == "초기화")
					System.out.print("");
				else
					System.out.print("\n" + sales_List.indexOf(s) + ". " + s);
			});
		do {
			System.out.print('\n' + "1.돌아가기" + '\n' + "2.매출 취소" + '\n' + "-> ");
			choice = NumberUtils.toInt(sc.nextLine());
			if (choice != 1 && choice != 2) {
				System.out.println("선택지 내의 번호를 정수로 입력해주십시오.");
			}
		} while (choice != 1 && choice != 2);

		if (choice == 1)
			return;
		do {
			System.out.print('\n' + "매출 취소를 원하는 판매기록의 번호를 입력하시오 : ");
			choice = NumberUtils.toInt(sc.nextLine());
			if (choice >= sales_List.size() || choice <= 0) {
				System.out.println("선택지 내의 번호를 정수로 입력해주십시오.");
			}
		} while (choice >= sales_List.size() || choice <= 0);

		sp = sales_List.get(choice);
		sp.set_canceled(true);
		p = get_product.apply(pd_List, sp.get_name());

		if (sp.get_card_pay() == true) {
			System.out.println("[ " + sp.get_name() + ' ' + sp.get_sales_volume() + "개 ] 매출 취소됨 / "
					+ sp.get_price() * sp.get_sales_volume() + "원을 고객의 카드로 환불하였습니다.");
			turnover -= sp.get_price() * sp.get_sales_volume();
			p.set_stock(p.get_stock() + sp.get_sales_volume());
		} else {
			System.out.println("[ " + sp.get_name() + ' ' + sp.get_sales_volume() + "개 ] 매출 취소됨 / "
					+ sp.get_price() * sp.get_sales_volume() + "원을 현금으로 환불하였습니다.");
			turnover -= sp.get_price() * sp.get_sales_volume();
			cash -= sp.get_price() * sp.get_sales_volume();
			p.set_stock(p.get_stock() + sp.get_sales_volume());
		}
	}

	void closing_And_saveRecord() throws IOException {
		dt = LocalDateTime.now();

		Path txt_p = Paths.get(
				fp.toString() + "\\" + dt.getHour() + "시" + dt.getMinute() + "분" + dt.getSecond() + "초_매출 기록" + ".txt");
		txt_p = Files.createFile(txt_p);

		List<String> S_List = sales_List.stream().map(s -> s.toString()).filter(s -> s.contains("초기화") == false)
				.collect(() -> new ArrayList<>(), (c, s) -> c.add(s), (lst1, lst2) -> lst1.addAll(lst2));
		S_List.add("[잔금 : " + cash + "원, 총 매출 : " + turnover + "원]");

		Files.write(txt_p, S_List);
		System.out.println('\n' + "매출 기록 저장 후 마감완료" + "\n\n" + "POS를 종료합니다.");
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
				System.out.println('\n' + "비밀번호를 5회 잘못 입력하였습니다" + '\n' + "시스템을 종료합니다.");
				System.exit(0);
			}
			System.out.println("비밀번호가 잘못 입력되었습니다." + '\n' + "5회 잘못 입력 시 시스템이 종료됩니다 / 틀린 횟수 -> " + wrong_count);
			return false;
		}
	}

	void change_Othority() {
		String PIN;
		if (supervisor_mode) {
			System.out.println('\n' + "스태프 모드로 변경합니다.");
			supervisor_mode = false;
		} else {
			do {
				System.out.print('\n' + "관리자 모드 비밀번호 입력 : ");
				PIN = sc.nextLine();
				if (wrong_count == 5) {
					System.out.println('\n' + "비밀번호를 5회 잘못 입력하였습니다" + '\n' + "시스템을 종료합니다.");
					System.exit(0);
				} else if (PIN.equals(SUPERVISOR_PIN) == false) {
					System.out
							.println("비밀번호가 잘못 입력되었습니다." + '\n' + "5회 잘못 입력 시 시스템이 종료됩니다 / 틀린 횟수 -> " + ++wrong_count);
				}
			} while (PIN.equals(SUPERVISOR_PIN) == false);
			System.out.println('\n' + "관리자 모드로 변경합니다.");
			supervisor_mode = true;
		}
	}

	void POS_ON() throws IOException {
		int choice;
		String PIN;
		System.out.println("My_POS를 시작합니다" + "\n\n" + "관리자 모드 비밀번호 -> 981006" + '\n' + "스태프 모드 비밀번호 -> 0000");
		do {
			System.out.print('\n' + "비밀번호 입력 : ");
			PIN = sc.nextLine();
		} while (check_Othority(PIN) == false);
		wrong_count = 0;

		while (true) {
			showManual();
			do {
				System.out.print("선택 > ");
				choice = NumberUtils.toInt(sc.nextLine());
				if (choice < -1 || choice == 0 || choice > 7) {
					System.out.println("선택지 내의 번호를 정수로 입력해주십시오.");
				}
			} while (choice < -1 || choice == 0 || choice > 7);

			switch (choice) {
			case 1:
				saling();
				break;
			case 2:
				if (supervisor_mode == false) {
					System.out.println('\n' + "'상품 제거'는 관리자 모드에서 사용 가능한 기능입니다.");
					break;
				}
				product_delete();
				break;
			case 3:
				if (supervisor_mode == false) {
					System.out.println('\n' + "'물품 입고'는 관리자 모드에서 사용 가능한 기능입니다.");
					break;
				}
				stocking();
				break;
			case 4:
				if (supervisor_mode == false) {
					System.out.println('\n' + "'상품 추가'는 관리자 모드에서 사용 가능한 기능입니다.");
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
				System.out.println('\n' + "시스템을 종료합니다.");
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
