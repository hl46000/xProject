import java.util.UUID;

public class EnterpriseKeyGenerator {

	public static void main(String[] args) {
		String enterprise_key = UUID.randomUUID().toString().toUpperCase().replace("-", "");
		enterprise_key += UUID.randomUUID().toString().toUpperCase().replace("-", "");

		System.out.println( "ENTERPRISE KEY : " + enterprise_key );

	}

}
