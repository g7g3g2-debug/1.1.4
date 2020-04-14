package com.kong.lutech.apartment.model.list;

import com.kong.lutech.apartment.model.Delivery;
import com.kong.lutech.apartment.model.PageInfo;

import java.util.List;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class DeliveryList {
    private List<Delivery> deliveries;
    private PageInfo pageInfo;


    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }
}
