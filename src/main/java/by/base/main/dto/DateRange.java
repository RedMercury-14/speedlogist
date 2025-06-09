package by.base.main.dto;

import java.sql.Date;

/**
 *  Объект, котоырй определяет сколько дней стока долно быть минимально а так же лог плечо и диапазон дат поставок
 * <br> Отдаёт количество дней стока (stock), начиная от заказа согласно графику поставок
 * <br> т.е. Если заказ в понедельник а поставка в среду, то он берет плече с понедельника по среду
 * <br> и до второй поставки, т.е. до сл. среды <b>(лог плечо + неделя)</b>
 */
public class DateRange{
	/**
	 * Дата начала лог плеча
	 * <br>Она же дата заказа
	 */
	public Date start;
	
	/**
	 * Дата окончания лог плеча
	 * <br>Она же дата поставки
	 */
	public Date end;
	
	/**
	 * Непосредственно лог плечо
	 */
	public Long days;
	
	/**
	 * Жинамический сток для текущего лог плеча (запас товара в днях до второй поставки)
	 */
	public Long stock; // динамический сток от даты заказа
	
	/**
	 * Дата заказа согласно графику поставок
	 */
	public String dayOfWeekHasOrder;
	
	/**
	 * Номер контракта
	 */
	public String numContruct;
	
	        
    public DateRange(Date start, Date end, Long days, String dayOfWeekHasOrder, String numContruct) {
    	this.start = start;
        this.end = end;
        this.days = days;
        this.dayOfWeekHasOrder = dayOfWeekHasOrder;;
        this.stock = days + 8;
        this.numContruct = numContruct;
    }


	@Override
	public String toString() {
		return "DateRange [start=" + start + ", end=" + end + ", days=" + days + ", stock=" + stock
				+ ", dayOfWeekHasOrder=" + dayOfWeekHasOrder + ", numContruct=" + numContruct + "]";
	}
    
    
}
