package com.whiteshipjavatest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

// @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class) //언더바 치환 / 프로퍼티에서 적용
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	//PER_CLASS -> BEFORE ALL, AFTER ALL이 static일 필요가 없음 / 테스트 메서드마다 의존성이 필요한 경우 (시나리오 테스트)
	//PER_METHOD(DEFAULT) -> static이어야 함
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// @ExtendWith(FindSlowTestExtension.class) // -> @RegisterExtension
class StudyTest {

	int value;

	@RegisterExtension
	static FindSlowTestExtension findSlowTestExtension =
		new FindSlowTestExtension(1000L);

	@Order(1) //MethodOrderer.OrderAnnotation이 참조함
	@Test
	@DisplayName("스터디 만들기 🧡") //권장됨
	void create_new_study() {

		//TODO : AssertJ , Truth, Hamcrest

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));

		System.out.println(value++);
		System.out.println(this);

		//300밀리동안 끝까지 기다렸다가 종료
		assertTimeout(Duration.ofMillis(1000), () -> {
			new Study(10);
			Thread.sleep(300);
		});


		//다 끝날때까지 기다리지 않고 내가 원하는 100밀리 세컨에 종료
		assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
			new Study(10);
			Thread.sleep(10);
		});


		String message = exception.getMessage();
		assertEquals("limit은 0보다 커야한다.", message);
		Study study = new Study(10);


		//앞의 assert가 실패하면 뒤의 assert가 성공 여부를 모르기 때문에 전부 실행 후 파악 가능하게 함
		assertAll(
			() -> assertNotNull(study),
			() -> assertEquals(StudyStatus.DRAFT, study.getStatus(), "스터디를 처음 만들면 상태값이 DRAFT여야 한다."), //string
			() -> assertEquals(StudyStatus.DRAFT, study.getStatus(), () -> "스터디를 처음 만들면 상태값이 DRAFT여야 한다."),  //supplier
			() -> assertTrue(study.getLimit() > 0, "스터디 최대 참석 인원은 0보다 커야 한다.")
		);


		// assertNotNull(study);
		// assertEquals(StudyStatus.DRAFT, study.getStatus(), "스터디를 처음 만들면 상태값이 DRAFT여야 한다."); //string
		// assertEquals(StudyStatus.DRAFT, study.getStatus(), ()->"스터디를 처음 만들면 상태값이 DRAFT여야 한다.");  //supplier
		// assertTrue(study.getLimit() > 0, "스터디 최대 참석 인원은 0보다 커야 한다.");
	}

	@Test
	@Disabled //테스트 잠시 비활성화 시킬때 사용
	void create_new_study_again() {


		System.out.println(value++);
		System.out.println(this);

		System.out.println("create1");
	}

	@Test
	@Disabled //테스트 잠시 비활성화 시킬때 사용
	void create_new_study_condition() {


		System.out.println(value++);
		System.out.println(this);

		String test_env = System.getenv("TEST_ENV");
		System.out.println("test env : "+test_env);
		assumeTrue("LOCAL".equalsIgnoreCase(test_env));

		assumingThat("LOCAL".equalsIgnoreCase(test_env),()->{
			System.out.println("local test env");
		});

		assumingThat("minseok".equalsIgnoreCase(test_env),()->{
			System.out.println("minseok test env");
		});
	}

	@Test
	// @Disabled //테스트 잠시 비활성화 시킬때 사용
	@DisabledOnOs(OS.MAC)//특정 os에서 테스트 비활성화
	@EnabledOnOs({OS.LINUX, OS.WINDOWS}) //특정 os에서 테스트 활성화
	@EnabledOnJre(JRE.JAVA_11)
	@DisabledOnJre(JRE.JAVA_8)
	@EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "LOCAL")
	void create_new_study_condition_os() {

		String test_env = System.getenv("TEST_ENV");
		System.out.println("test env : "+test_env);
		assumeTrue("LOCAL".equalsIgnoreCase(test_env));

		assumingThat("LOCAL".equalsIgnoreCase(test_env),()->{
			System.out.println("local test env");
		});

		assumingThat("minseok".equalsIgnoreCase(test_env),()->{
			System.out.println("minseok test env");
		});
	}

	// @Test
	// @Tag("fast")
	@FastTest // (@Test  + Tag("fast))
	@DisplayName("스터디 만들기 fast")
	void create_study_tag_fast() {
		Study study = new Study(100);
		assertThat(study.getLimit()).isGreaterThan(0);
	}


	// @Test
	// @Tag("slow")
	@SlowTest// (@Test  + Tag("slow))
	@DisplayName("스터디 만들기 slow")
	void create_study_tag_slow() {

		System.out.println(value++);
		System.out.println(this);

		Study study = new Study(100);
		assertThat(study.getLimit()).isGreaterThan(0);
	}

	@DisplayName("스터디 만들기")
	@RepeatedTest(value=10, name = "{displayName}, {currentRepetition}/{totalRepetitions}")
	void create_study_iteration(RepetitionInfo repetitionInfo) {


		System.out.println(value++);
		System.out.println(this);

		System.out.println("test " + repetitionInfo.getCurrentRepetition() + "/" +repetitionInfo.getTotalRepetitions());
	}

	@DisplayName("파라미터 테스트")
	@ParameterizedTest(name = "{index} {displayName} message={0}")
	@ValueSource(strings = {"날씨가", "많이", "추워지고", "있네요"})
	void parameterizedTest(String message) {
		System.out.println(message);
	}

	@DisplayName("파라미터 테스트")
	@ParameterizedTest(name = "{index} {displayName} message={0}")
	@ValueSource(ints = {10,20,40})
	// @EmptySource
	// @NullSource
	// @NullAndEmptySource
	void parameterizedTest2(Integer limit) {
		System.out.println(limit);
	}



	@DisplayName("파라미터 테스트")
	@ParameterizedTest(name = "{index} {displayName} message={0}")
	@ValueSource(ints = {10,20,40})
		// @EmptySource
		// @NullSource
		// @NullAndEmptySource
	void parameterizedTest3(@ConvertWith(StudyConverter.class) Study study) {
		System.out.println(study.getLimit());
	}

	@DisplayName("파라미터 테스트")
	@ParameterizedTest(name = "{index} {displayName} message={0}")
	@CsvSource({"10, '자바 스터디'", "20, 스프링"})
	void parameterizedTest4(Integer limit, String name) {
		Study study = new Study(limit, name);
		System.out.println(study);
	}


	@DisplayName("파라미터 테스트")
	@ParameterizedTest(name = "{index} {displayName} message={0}")
	@CsvSource({"10, '자바 스터디'", "20, 스프링"})
	void parameterizedTest5(ArgumentsAccessor accessor) {
		Study study = new Study(accessor.getInteger(0), accessor.getString(1));
		System.out.println(study);
	}


	@DisplayName("파라미터 테스트")
	@ParameterizedTest(name = "{index} {displayName} message={0}")
	@CsvSource({"10, '자바 스터디'", "20, 스프링"})
	void parameterizedTest6(@AggregateWith(StudyAggregator.class) Study study) {
		System.out.println(study);
	}


	static class StudyAggregator implements ArgumentsAggregator {
		@Override
		public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {

			return  new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
		}
	}


	static class StudyConverter extends SimpleArgumentConverter {
		@Override
		protected Object convert(Object o, Class<?> aClass) throws ArgumentConversionException {
			assertEquals(Study.class, aClass, "Can only convert to study");
			return new Study(Integer.parseInt(o.toString()));
		}
	}



	@BeforeAll
	static void beforeAll() {

		System.out.println("before all");
	}

	@AfterAll
	static void afterAll() {
		System.out.println("after all");
	}

	@BeforeEach
	void beforeEach() {
		System.out.println("beforeEach");
	}


	@AfterEach
	void afterEach() {
		System.out.println("afterEach");
	}


}