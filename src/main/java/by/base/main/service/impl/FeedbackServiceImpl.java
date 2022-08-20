package by.base.main.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.FeedbackDAO;
import by.base.main.model.Feedback;
import by.base.main.model.Shop;
import by.base.main.model.User;
import by.base.main.service.FeedbackService;

@Service
public class FeedbackServiceImpl implements FeedbackService{
	
	@Autowired
	private FeedbackDAO feedbackDAO;

	@Override
	public List<Feedback> getFeedbackList() {
		return feedbackDAO.getFeedbackList();
	}

	@Override
	public void saveOrUpdateFeedback(Feedback feedback) {
		feedbackDAO.saveOrUpdateFeedback(feedback);
		
	}

	@Override
	public List<Feedback> getFeedbackAsDriver(User driver) {
		return feedbackDAO.getFeedbackAsDriver(driver);
	}

	@Override
	public List<Feedback> getFeedbackAsShop(Shop shop) {
		return feedbackDAO.getFeedbackAsShop(shop);
	}

	@Override
	public Feedback getFeedbackById(int id) {
		return feedbackDAO.getFeedbackById(id);
	}

	@Override
	public List<Feedback> getFeedbackFROM(int id) {
		return feedbackDAO.getFeedbackFROM(id);
	}

	@Override
	public void deleteFeedbackById(int id) {
		feedbackDAO.deleteFeedbackById(id);
		
	}

	@Override
	public List<Feedback> getFeedbackListByRHS(int idRHS) {
		return feedbackDAO.getFeedbackListByRHS(idRHS);
	}

}
