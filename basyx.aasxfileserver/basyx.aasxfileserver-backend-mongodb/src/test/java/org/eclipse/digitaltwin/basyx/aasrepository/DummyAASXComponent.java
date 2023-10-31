package org.eclipse.digitaltwin.basyx.aasrepository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.eclipse.digitaltwin.basyx")
public class DummyAASXComponent {
	
	public static void main(String[] args) {
		SpringApplication.run(DummyAASXComponent.class, args);
	}
}
