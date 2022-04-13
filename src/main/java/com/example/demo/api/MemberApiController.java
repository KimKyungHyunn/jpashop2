package com.example.demo.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Member;
import com.example.demo.service.MemberService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

	private final MemberService memberService;
	
	//아래의 문제는 엔티티를 바로 반환한다는 점, view에 엔티티가 종속되게 흘러갈 가능성이 크다는 점
	//그리고 array로 return 되면 엔티티 정보 외에 다른 기능을 추가할 수 없음, 유연성이 떨어지게 됨
	@GetMapping("/api/v1/members")
	public List<Member> membersV1(){
		return memberService.findMembers();
	}
	
	//응답값의 스펙이 엔티티 내부 정보 출력으로 굳어버리는 것을 막는 방법으로 새로운 dto 생성
	@GetMapping("/api/v2/members")
	public Result membersV2(){
		List<Member> findMembers = memberService.findMembers();
		List<MemberDto> collect = findMembers.stream()
			.map(m -> new MemberDto(m.getName()))
			.collect(Collectors.toList());
		
		return new Result(collect.size(), collect);
	}
	
	@Data
	@AllArgsConstructor
	static class Result<T>{
		private int count;
		private T data;
	}
	
	@Data
	@AllArgsConstructor
	static class MemberDto{
		private String name;
	}
	
	//api를 위한 DTO를 생성해야함.. entity를 그대로 쓸 경우 entity 변화에 따라 api 스펙에 영향을 받는 문제 발생
	@PostMapping("/api/v1/members")
	public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}
	
	//클라이언트에서 member로 파라미터들을 받아버리면 어떤 파라미터들을 받아오는지 알 수 없음
	//아래의 경우 name만을 파라미터로 받겠다고 명시해놔서 직관적으로 알 수 있음
	//실무에서는 엔티티를 노출하거나 파라미터로 직접 받아버리면 안된다
	@PostMapping("/api/v2/members")
	public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
		Member member = new Member();
		member.setName(request.getName());
		
		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}
	
	@PutMapping("/api/v2/members/{id}")
	public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
			@RequestBody @Valid UpdateMemberRequest request) {
		memberService.update(id, request.getName());
		Member findMember = memberService.findOne(id);
		return new UpdateMemberResponse(findMember.getId(), findMember.getName());
	}
	
	@Data
	static class UpdateMemberRequest{
		private String name;
	}
	
	@Data
	@AllArgsConstructor
	static class UpdateMemberResponse{
		private Long id;
		private String name;
	}
	
	@Data
	static class CreateMemberRequest{
		private String name;
	}
	
	@Data
	static class CreateMemberResponse{
		private Long id;
		
		public CreateMemberResponse(Long id) {
			this.id = id;
		}
	}
}
