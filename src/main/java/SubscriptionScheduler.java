import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.puzzlelog.api.dao.entity.KakaoPaySubscription;
import com.puzzlelog.api.service.AssetService;
import com.puzzlelog.api.service.KakaoPayService;
import com.puzzlelog.api.service.UserService;

@Component
public class SubscriptionScheduler {

	@Autowired
	private KakaoPayService kakaoPayService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AssetService assetService;
	
	@Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
	public void autoSubscription() {
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
		List<KakaoPaySubscription> subscriptions = kakaoPayService.getAllSubscriptions();
		
		for (KakaoPaySubscription subscription : subscriptions) {
			LocalDate updatedDate = subscription.getUpdatedAt().toLocalDate();
			if (updatedDate.plusMonths(1).isBefore(today)) {
				// 구독 만료 처리
				userService.updateSubscriptionStatus(subscription.getPartnerUserId(), false);
				assetService.lockAllPaidStickers();
				System.out.println("구독 만료 : " + subscription.getPartnerOrderId());
			}
		}
	}
}
