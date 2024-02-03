package com.example.iworks.domain.schedule.repository.scheduleAssign;
import com.example.iworks.domain.schedule.domain.ScheduleAssign;
import com.example.iworks.domain.schedule.dto.scheduleAssign.response.ScheduleAssignResponseDto;
import com.example.iworks.domain.schedule.dto.scheduleAssign.request.ScheduleAssignSearchParameterDto;
import com.example.iworks.domain.schedule.repository.scheduleAssign.custom.ScheduleAssignRepositoryCustom;
import com.example.iworks.global.dto.SearchConditionDate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static com.example.iworks.domain.schedule.domain.QSchedule.schedule;
import static com.example.iworks.domain.schedule.domain.QScheduleAssign.scheduleAssign;


@Repository
public class ScheduleAssignRepositoryImpl implements ScheduleAssignRepositoryCustom {

   private final JPAQueryFactory jpaQueryFactory;

   public ScheduleAssignRepositoryImpl(EntityManager entityManager) {
      this.jpaQueryFactory = new JPAQueryFactory(entityManager);
   }

   @Override
   public List<ScheduleAssignResponseDto> findScheduleAssignsBySearchParameter(List<ScheduleAssignSearchParameterDto> requestDtoList) {
      Set<ScheduleAssign> foundScheduleAssignList = new HashSet<>();

      for (ScheduleAssignSearchParameterDto requestDto : requestDtoList){
         List<ScheduleAssign> foundScheduleAssign =
                 jpaQueryFactory
                         .selectFrom(scheduleAssign)
                         .join(scheduleAssign.schedule, schedule).fetchJoin()
                         .where(eqCategoryCodeId(requestDto.getScheduleCategoryCodeId())
                                 .and(eqAssigneeId(requestDto.getScheduleAssigneeId())))
                         .distinct()
                         .fetch();

         foundScheduleAssignList.addAll(foundScheduleAssign);
      }
      return mapToScheduleAssignResponseDto(foundScheduleAssignList);

   }
   @Override
   public List<ScheduleAssignResponseDto> findScheduleAssignsBySearchParameter(List<ScheduleAssignSearchParameterDto> requestDtoList, SearchConditionDate searchConditionDate) {
      Set<ScheduleAssign> foundScheduleAssignList = new HashSet<>();

      for (ScheduleAssignSearchParameterDto requestDto : requestDtoList){
         List<ScheduleAssign> foundScheduleAssign =
                 jpaQueryFactory
                         .selectFrom(scheduleAssign)
                         .join(scheduleAssign.schedule, schedule).fetchJoin()
                         .where(eqCategoryCodeId(requestDto.getScheduleCategoryCodeId())
                                 .and(eqAssigneeId(requestDto.getScheduleAssigneeId())).and(withInDate(searchConditionDate)))
                         .distinct()
                         .fetch();

         foundScheduleAssignList.addAll(foundScheduleAssign);
      }

      System.out.println("------------------------");
      for (ScheduleAssign scheduleAssign:foundScheduleAssignList){
         System.out.println(scheduleAssign);
      }
      return mapToScheduleAssignResponseDto(foundScheduleAssignList);

   }
   private BooleanExpression eqCategoryCodeId (int categoryCodeId){
      return scheduleAssign.scheduleAssigneeCategory.codeId.eq(categoryCodeId);
   }
   private BooleanExpression eqAssigneeId (int assigneeId){
      return scheduleAssign.scheduleAssigneeId.eq(assigneeId);
   }
   private BooleanExpression withInDate (SearchConditionDate searchConditionDate){
      return schedule.scheduleStartDate.between(searchConditionDate.getStartDate(), searchConditionDate.getEndDate());
   }

   private List<ScheduleAssignResponseDto> mapToScheduleAssignResponseDto(Set<ScheduleAssign> scheduleAssignList) {
      Set<ScheduleAssignResponseDto> foundScheduleAssignResponseDtoList = new HashSet<>();
      for (ScheduleAssign scheduleAssign : scheduleAssignList){
         foundScheduleAssignResponseDtoList.add(
                 ScheduleAssignResponseDto.builder()
                         .scheduleId(scheduleAssign.getSchedule().getScheduleId())
                         .scheduleAssigneeId(scheduleAssign.getScheduleAssigneeId())
                         .scheduleAssigneeCategoryId(scheduleAssign.getScheduleAssigneeCategory().getCodeId())
                         .scheduleAssigneeCategoryName(scheduleAssign.getScheduleAssigneeCategory().getCodeName())
                         .scheduleTitle(scheduleAssign.getSchedule().getScheduleTitle())
                         .scheduleDivisionName(scheduleAssign.getSchedule().getScheduleDivision().getCodeName())
                         .scheduleStartDate(scheduleAssign.getSchedule().getScheduleStartDate())
                         .scheduleEndDate(scheduleAssign.getSchedule().getScheduleEndDate())
                         .build()
         );
      }
      return new ArrayList<>(foundScheduleAssignResponseDtoList);
   }



}
