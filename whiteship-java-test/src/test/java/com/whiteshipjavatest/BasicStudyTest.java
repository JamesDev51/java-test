package com.whiteshipjavatest;

import static org.apache.logging.log4j.util.StringBuilders.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.time.Duration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariables;
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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import lombok.extern.slf4j.Slf4j;

/**
 * @TestMethodOrder(MethodOrderer.???) :  테스트 실행 순서 정하기
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
/**
 * @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class) : 메서드 이름에 언더바가 있는 경우 이를 띄어쓰기로 치환해줌
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Slf4j
// @ExtendWith(FindSlowTestExtension.class) // -> @RegisterExtension
class BasicStudyTest {

	@RegisterExtension
	static FindSlowTestExtension findSlowTestExtension =
		new FindSlowTestExtension(1000L);

	/**
	 * @TestInstance(TestInstance.Lifecycle.???)
	 *  PER_METHOD -> 테스트 인스턴스의 생명주기가 한 메서드를 테스트 할 때마다 바뀜
	 *  @BeforeAll,@AfterAll 이 static이어야 함.
	 *
	 * PER_CLASS -> 테스트 인스턴스의 생명주기가 한 클래스인 경우 (테스트 메서드마다 의존성이 필요한 경우 ex)시나리오 테스트)
	 * @BeforeAll,@AfterAll이 static이 될 필요가 없고 인스턴스 메서드로 사용 가능
	 */

	@BeforeAll
	static void beforeAll() {
		log.info("before all");
	}

	@AfterAll
	static void afterAll() {
		log.info("after all");
	}

	@BeforeEach
	void beforeEach() {
		log.info("before each");
	}

	@AfterEach
	void afterEach() {
		log.info("after each");
	}



	@Test
	@DisplayName("스터디 만들기🧡")
	void create_new_study() throws Exception{

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
		String message = exception.getMessage();

		assertEquals("limit은 0보다 커야한다.", message);

	}

	@Test
	@DisplayName("시간 초과 나는 스터디 만들기 (끝까지 기다리기)")
	void create_new_study_timeout_basic() throws Exception{

		//1000ms 세컨 안에 실행됨이 보장되어야 함.
		assertTimeout(Duration.ofMillis(1000), () ->{
			new Study(10);
			Thread.sleep(300);
			log.info("300ms 잠");
		});
	}

	@Test
	@DisplayName("시간 초과 나는 스터디 만들기 (시간초과되면 끊기)")
	void create_new_study_timeout_preemptive() throws Exception{
		assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
			new Study(10);

			//만약 쓰레드가 10초간 슬립해도 제한시간 1초가 초과되면 테스트가 종료됨 끝까지 안기다림
			// Thread.sleep(10000);

			Thread.sleep(10);
		});
	}

	@Test
	@DisplayName("스터디 만들기 전체 실행 ")
	void create_new_study_all() throws Exception{

		Study newStudy = new Study(10);
		assertAll(
			() -> assertNotNull(newStudy),
			() -> assertEquals(StudyStatus.DRAFT, newStudy.getStatus()),
			() -> assertEquals(StudyStatus.DRAFT, newStudy.getStatus(), "스터디를 처음 만들면 기본값이 DRAFT 상태어야 한다."),
			() -> assertTrue(newStudy.getLimit() > 0),
			() -> assertTrue(newStudy.getLimit() > 0, "스터디 참석 인원은 적어도 0보다 커야한다.")

		);
	}

	@Test
	@DisplayName("스터디 만들기 비활성화됨")
	@Disabled
	void create_new_study_disabled() throws Exception{
		throw new Exception("에러 터뜨리기");
	}

	@Test
	@DisplayName("스터디 만들기 환경변수 구분")
	void create_new_study_test_env() throws Exception{
	    //given
		String test_env = System.getenv("TEST_ENV");
		log.info("test_env : {}", test_env);

		//when
		assertTrue("LOCAL".equalsIgnoreCase(test_env));


		//then
		/**
		 * assume은 조건이 충족되지 않으면 테스트를 건너 뜀
		 */
		assumingThat("LOCAL".equalsIgnoreCase(test_env), () -> {
			log.info("local test env");
		});

		assumingThat("DEV".equalsIgnoreCase(test_env), () -> {
			log.info("dev test env");
		});


	}

	@Test
	@DisabledOnOs(OS.MAC)
	@EnabledOnOs({OS.LINUX, OS.WINDOWS})
	@DisabledOnJre(JRE.JAVA_17)
	@EnabledOnJre({JRE.JAVA_8, JRE.JAVA_9, JRE.JAVA_11})
	@DisplayName("조건에 따라 테스가 실행되는 곳과 안되는 곳이 있음")
	@EnabledIfEnvironmentVariable(named="TEST_ENV", matches="LOCAL")
	void create_new_study_condition() throws Exception {
	}


	@Test
	@Tag("fast")
	@DisplayName("스터디 어노테이션 만들기 fast")
	void create_new_study_tag_fast() throws Exception{

		Study newStudy = new Study(100);
		assertThat(newStudy.getLimit()).isGreaterThan(0);

	}

	@Test
	@Tag("slow")
	@DisplayName("스터디 어노테이션 만들기 slow")
	void create_new_study_tag_slow() throws Exception{

		Study newStudy = new Study(100);
		assertThat(newStudy.getLimit()).isGreaterThan(0);
	}

	@FastTest
	@DisplayName("스터디 어노테이션 만들기 fast")
	void create_new_study_tag_fast_annotation() throws Exception{

		Study newStudy = new Study(100);
		assertThat(newStudy.getLimit()).isGreaterThan(0);

	}

	@SlowTest
	@DisplayName("스터디 어노테이션 만들기 slow")
	void create_new_study_tag_slow_annotation() throws Exception{

		Study newStudy = new Study(100);
		assertThat(newStudy.getLimit()).isGreaterThan(0);
	}

	@RepeatedTest(value=10, name="{displayName}, {currentRepetition}/{totalRepetitions}")
	@DisplayName("스터디 만들기 10번 반복")
	void create_new_study_iteration(RepetitionInfo repetitionInfo) throws Exception{

		log.info("test : " + repetitionInfo.getCurrentRepetition() + "/" + repetitionInfo.getTotalRepetitions());
	}

	@ParameterizedTest(name = "{index} {displayName} message={0}")
	@DisplayName("스터디 만들기 파라미터 1 -> ")
	@ValueSource(strings = {"날씨가", "많이", "추워지고", "있네요"})
	void create_new_study_parameter1(String message) throws Exception {
		log.info("message : {}", message);
	}

	@ParameterizedTest(name = "{index} {displayName} value={0}")
	@ValueSource(ints = {10, 20, 40})
	@DisplayName("스터디 만들기 파라미터 2 -> ")
	void create_new_study_parameter2(Integer limit) throws Exception {
		log.info("value : {}", limit);
	}

	@ParameterizedTest(name = "{index} {displayName} message={0}")
	@ValueSource(ints = {10, 20, 40})
	@DisplayName("스터디 만들기 파라티터 3 -> ")
	void create_new_study_parameter3(@ConvertWith(StudyConverter.class)Study study) throws Exception {
		log.info("limit : {}", study.getLimit());
	}

	static class StudyConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object o, Class<?> aClass) throws ArgumentConversionException {
			assertEquals(Study.class, aClass, "Can only convert to study");
			return new Study(Integer.parseInt(o.toString()));
		}
	}

	@DisplayName("스터디 만들기 파라미터 4 -> ")
	@ParameterizedTest(name = "{index} {displayName} message = {0} / {1}")
	@CsvSource({"10, '자바 스터디'", "20, '스프링'"})
	void create_new_study_parameter4(@AggregateWith(StudyAggregator.class) Study study) throws Exception{
		log.info("study : {}", study);

	}

	static class StudyAggregator implements ArgumentsAggregator {
		@Override
		public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
			return new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
		}
	}

	@Test
	@DisplayName("존나 느리게 만들어지는 스터디 하지만 @SlowTest 어노테이션이 없음")
	void create_new_study_slow_but_no_slolw_annotation() throws Exception{
		Thread.sleep(1001);
	}






}
