package my.java_excute.apps;

import java.util.Map;

public class CodeTest {

	public static void main(String[] args) {
		String sealingParam = "-url http://localhost:8080/AppSealingApiServer -srcapk e:\\workTemp\\PizzaSealingTag.apk -sealedapk e:\\workTemp\\PizzaSealingTag_ci_tst_sealed.apk -block_environment -third_party_integration -encrypt_monobehaviour none -authkey D973738294B84193B71EA8FF5BF824505534B6ABEAA049478103CCAC20E41365 -deploymode release -debug on -config ../dist/config.txt]";
		
		String token [] = sealingParam.split("authkey");
		if( token.length > 1 ) {
			String values [] = token[1].trim().split(" ");
			
			System.out.println( "'" + values[0].trim() + "'" );			
		}
	}

}
