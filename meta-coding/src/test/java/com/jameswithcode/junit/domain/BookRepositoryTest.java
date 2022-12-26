package com.jameswithcode.junit.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BookRepositoryTest {

	@Autowired
	private BookRepository bookRepository;

	// @BeforeAll //테스트 시작 전에 한번만 실행
	@BeforeEach //테스트 시작전에 한번씩 실행
	public void 데이터준비() {
		System.out.println("======================================");
		String title = "junit";
		String author = "겟인데어";

		Book book = Book.builder()
			.title(title)
			.author(author)
			.build();
		bookRepository.save(book);

	}

	//1 .책 등록
	@Test
	public void 책등록_test() throws Exception{

	    //given (데이터 준비)
		String title = "junit5";
		String author = "메타코딩";

		Book book = Book.builder()
			.title(title)
			.author(author)
			.build();

		//when (테스트 실행)
		Book bookPS = bookRepository.save(book);

		//then (검증)
		assertEquals(title, bookPS.getTitle());
		assertEquals(author, bookPS.getAuthor());
	}


	//2. 책 목록보기
	@Test
	public void 책목록보기_test() throws Exception{
	    //given
		String title = "junit5";
		String author = "메타코딩";

	    //when
		List<Book> books = bookRepository.findAll();

	    //then
		System.out.println(books.size());
		assertEquals(title, books.get(0).getTitle());
		assertEquals(author, books.get(0).getAuthor());

	}


	//3. 책 한건보기
	@Test
	public void 책한건보기_test() throws Exception{
	    //given
		String title = "junit";
		String author = "겟인데어";

	    //when
		Book bookPS = bookRepository.findById(1L).get();

		//then
		assertEquals(title, bookPS.getTitle());
		assertEquals(author, bookPS.getAuthor());

	}


	//4. 책 수정

	//5. 책 삭제
}