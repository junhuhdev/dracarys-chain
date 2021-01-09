![](https://github.com/junhuhdev/dracarys-chain/blob/master/logo.png?raw=true)

# dracarys-chain

Installation
------------

```xml

<dependency>
    <groupId>huh.enterprises</groupId>
    <artifactId>dracarys-chain</artifactId>
    <version>1.0.0</version>
</dependency>
```

Usage
------

## Enable Dracarys Chain

```java

@EnableDracarysChain
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(JobRunrApplication.class, args);
	}

}
```

## Commands

```java
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserValidateCmd implements Command {

	private final ValidationService validationService;

	public ChainContext execute(ChainContext ctx, Chain chain) {
		var event = ctx.getEvent(ValidateUserRequest.class);
		boolean response = validationService.validate(event);
		if (response) {
			return chain.proceed(ctx);
		}
		return ctx;
	}

}

@RequiredArgsConstructor
@Component
public class UserCreateCmd implements Command {

	private final UserRepository userRepository;

	public ChainContext execute(ChainContext ctx, Chain chain) {
		var event = ctx.getEvent(UserRequest.class);
		userRepository.create(event);
		return chain.proceed(ctx);
	}

}

@RequiredArgsConstructor
@Component
public class EmailSendCmd implements Command {

	private final EmailService emailService;

	public ChainContext execute(ChainContext ctx, Chain chain) {
		var event = ctx.getEvent(MailRequest.class);
		emailService.send(event);
		return chain.proceed(ctx);
	}

}


```

## Chain

```java
import huh.enterprises.dracarys.chain.chain.ChainBase;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationChain extends ChainBase {

	@Override
	protected Class<?>[] getCommands() {
		return new Class<?>[]{
				UserValidateCmd.class,
				UserCreateCmd.class,
				EmailSendCmd.class
		};
	}

}
```
