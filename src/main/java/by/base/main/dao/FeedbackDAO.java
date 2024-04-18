package by.base.main.dao;

import java.util.List;

import by.base.main.model.Feedback;
import by.base.main.model.Shop;
import by.base.main.model.User;

public interface FeedbackDAO {
	
	List<Feedback> getFeedbackList();
	void saveOrUpdateFeedback (Feedback feedback);
	List<Feedback> getFeedbackAsDriver(User driver);
	List<Feedback> getFeedbackAsShop(Shop shop);
	Feedback getFeedbackById(int id);
	List<Feedback> getFeedbackFROM(int id);
	void deleteFeedbackById(int id);
	List<Feedback> getFeedbackListByRHS(int idRHS);

}
