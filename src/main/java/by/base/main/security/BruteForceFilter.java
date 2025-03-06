package by.base.main.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


public class BruteForceFilter implements Filter {

	private static volatile BruteForceFilter instance;

	public static BruteForceFilter getInstance() {
		BruteForceFilter localInstance = instance;
		if (localInstance == null) {
			synchronized (BruteForceFilter.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new BruteForceFilter();
				}
			}
		}
		return localInstance;
	}

	private Map<String, Integer> counter = new HashMap<String, Integer>();
	private List<String> blockId = new ArrayList<String>();
	private Integer stopCounter = 5;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		Filter.super.init(filterConfig);
		System.out.println("BruteForceFilter: init");
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		Filter.super.destroy();
		System.out.println("BruteForceFilter: destroy");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("Brute-force");
		String targetId = request.getLocalAddr();
		if (blockId.contains(targetId)) {
			System.out.println("ID заблокирован. Доступ запрещен!");
		} else {
			System.out.println("Brute-force id " + request.getLocalAddr());
			if (!counter.containsKey(targetId)) {
				counter.put(targetId, 1);
			} else {
				Integer i = counter.get(targetId);
				if (i >= stopCounter) {
					System.out.println("Обнаружена попытка BruteForce!");
					System.out.println("ID " + targetId + " Заблокирован!");
					blockId.add(targetId);
				} else {
					i = i + 1;
					counter.put(targetId, i);
					System.out.println(targetId + "    " + i);
				}
			}
			chain.doFilter(request, response);
		}

	}

	public List<String> getBlockId() {
		return blockId;
	}

	public void setBlockId(List<String> blockId) {
		this.blockId = blockId;
	}

}
