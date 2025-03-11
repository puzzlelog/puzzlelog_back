package com.puzzlelog.puzzlelog.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.puzzlelog.puzzlelog.document.ChallengeDocument;
import com.puzzlelog.puzzlelog.document.PointDocument;
import com.puzzlelog.puzzlelog.document.UserChallengeDocument;
import com.puzzlelog.puzzlelog.repository.ChallengeRepository;
import com.puzzlelog.puzzlelog.repository.PointRepository;
import com.puzzlelog.puzzlelog.repository.UserChallengeRepository;
import com.puzzlelog.puzzlelog.repository.UserRepository;
import com.puzzlelog.puzzlelog.service.challenge.ActivateChallengeService;
import com.puzzlelog.puzzlelog.service.challenge.AddUserPointService;
import com.puzzlelog.puzzlelog.service.challenge.CancelUserChallengeService;
import com.puzzlelog.puzzlelog.service.challenge.CompleteChallengeService;
import com.puzzlelog.puzzlelog.service.challenge.CreateChallengeService;
import com.puzzlelog.puzzlelog.service.challenge.FindUserChallengesService;
import com.puzzlelog.puzzlelog.service.challenge.GetActiveChallengesService;
import com.puzzlelog.puzzlelog.service.challenge.GetUserPointsService;
import com.puzzlelog.puzzlelog.service.challenge.UpdateChallengeProgressService;

@RestController
@RequestMapping("/api")
public class ChallengePointController {
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    
    @Autowired 
    public ChallengePointController(ChallengeRepository challengeRepository, 
                                    UserChallengeRepository userChallengeRepository,
                                    PointRepository pointRepository,
                                    UserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.userChallengeRepository = userChallengeRepository;
        this.pointRepository = pointRepository;
        this.userRepository = userRepository;
    }

    // ✅ 현재 활성화된 챌린지만 (is_active= true) 조회 //postman으로 확인
    @GetMapping("/getchallenge")
    public ResponseEntity<List<ChallengeDocument>> getActiveChallenges() {
        GetActiveChallengesService command = new GetActiveChallengesService(challengeRepository);
        return ResponseEntity.ok(command.execute());
    }

    
    // ✅ 챌린지를 활성화하고 사용자에게 자동 배포       //postman으로 확인 , user테이블이랑 연결 완료
    @PostMapping("/activateChallenge")
    public ResponseEntity<Void> activateChallenge(@RequestParam(value = "challengeId") String challengeId) {
        ActivateChallengeService command = new ActivateChallengeService(challengeRepository, userChallengeRepository, userRepository, challengeId);
        command.execute();
        return ResponseEntity.ok().build();
    }
    
    
    
    
    // ✅ 새로운 챌린지 생성 (POST)  //postman으로 확인
    @PostMapping("/addchallenge")
    public ResponseEntity<ChallengeDocument> addChallenge(@RequestBody ChallengeDocument challenge) {
    	CreateChallengeService command = new CreateChallengeService(challengeRepository, challenge);
        return ResponseEntity.ok(command.execute());
    }

    
    // ✅ 사용자의 챌린지 진행 상태 조회 (GET)   //postman으로 확인
    @GetMapping("/getUserChallenges/{userId}")
    public ResponseEntity<List<UserChallengeDocument>> getUserChallenges(@PathVariable(value = "userId") String userId) {
        FindUserChallengesService command = new FindUserChallengesService(userChallengeRepository, userId, true); // ✅ 완료된 챌린지 포함
        return ResponseEntity.ok(command.execute());
    }


    
    
    // ✅ 챌린지 진행도 업데이트 (POST)    //postman으로 확인
    @PostMapping("/updateChallengeProgress")
    public ResponseEntity<Void> updateChallengeProgress(@RequestParam (value = "userId") String userId, @RequestParam (value = "challengeId") String challengeId) {
        UpdateChallengeProgressService command = new UpdateChallengeProgressService(userChallengeRepository, challengeRepository, pointRepository, userId, challengeId);
        command.execute();
        return ResponseEntity.ok().build();
    }
    
    
    
//    // ✅ 사용자가 팝업을 닫으면 기록 저장 (POST)
//    @PostMapping("/dismissPopup")
//    public ResponseEntity<Void> dismissPopup(@RequestParam String userId, @RequestParam String challengeId) {
//        DismissPopupCommand command = new DismissPopupCommand(userChallengeRepository, userId, challengeId);
//        command.execute();
//        return ResponseEntity.ok().build();
//    }
    

    // ✅ 챌린지 완료 시 포인트 지급 (POST)
    @PostMapping("/completeChallenge")
    public ResponseEntity<Void> completeChallenge(@RequestParam String userId, @RequestParam String challengeId) {
        CompleteChallengeService command = new CompleteChallengeService(userChallengeRepository, challengeRepository, pointRepository, userId, challengeId);
        command.execute();
        return ResponseEntity.ok().build();
    }
    
   

    // ✅ 사용자의 포인트 조회 (GET)   //postman으로 확인
    @GetMapping("/getPoint")
    public ResponseEntity<List<PointDocument>> getUserPoints(@RequestParam(value = "userId") String userId) {
        GetUserPointsService command = new GetUserPointsService(pointRepository, userId);
        return ResponseEntity.ok(command.execute());
    }
    
    // ✅ 사용자의 포인트 추가   //postman으로 확인
    @GetMapping("/addPoint")
    public ResponseEntity<Void> addPoint(@RequestParam(value="userId", required = false) String userId, 
    		                             @RequestParam(value="amount") int amount,
    		                             @RequestParam(value = "challengeId") String challengeId) {
        AddUserPointService command = new AddUserPointService(pointRepository, userId, amount, challengeId);
        command.execute();
        return ResponseEntity.ok().build();
    }
   
    
    
  
    // ✅ 사용자의 챌린지 삭제 (GET)   //postman으로 확인
    @GetMapping("/cancelChallenge")
    public ResponseEntity<Void> cancelChallenge(@RequestParam String userId, @RequestParam String challengeId) {
        CancelUserChallengeService command = new CancelUserChallengeService(userChallengeRepository, userId, challengeId);
        command.execute();
        return ResponseEntity.ok().build();
    }



    
   


}
