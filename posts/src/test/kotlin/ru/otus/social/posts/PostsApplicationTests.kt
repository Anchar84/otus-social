package ru.otus.social.posts

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.junit.jupiter.api.Test

//@SpringBootTest
class PostsApplicationTests {

	@Test
	fun contextLoads() {
		val algorithm = Algorithm.HMAC256("otus-social")

		val verifier = JWT.require(algorithm)
			.withIssuer("otus-social")
			.build()

		val jwtToken = JWT.create()
			.withIssuer("otus-social")
			.withSubject("Baeldung Details")
			.withClaim("userId", "1234")
			.withClaim("userName", "Jon")
			.sign(algorithm);

		val decodedJWT = verifier.verify(jwtToken)
		val claim = decodedJWT.getClaim("userId")
		val claim2 = decodedJWT.getClaim("userName")

		println("claim: $claim, $claim2")
	}

}
