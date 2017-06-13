package br.com.emmanuelneri.service;

import br.com.emmanuelneri.model.Bill;
import br.com.emmanuelneri.model.BillItem;
import br.com.emmanuelneri.model.Carrier;
import br.com.emmanuelneri.model.Customer;
import br.com.emmanuelneri.model.ItemType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class BillServiceTest {

    @Autowired
    private CarrierService carrierService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BillService billService;

    private Carrier carrier;
    private Customer customer;

    @Before
    public void init() {
        carrier = carrierService.save(
                Carrier.builder()
                        .name("Carrier Test")
                        .build());

        customer = customerService.save(
                Customer.builder()
                        .name("Customer Test")
                        .build());
    }

    @Test
    public void saveABillWithTwoCalls() {
        final List<BillItem> items = new ArrayList<>();

        items.add(BillItem.builder()
                .dataTime(LocalDateTime.now())
                .originNumber("4499898484")
                .destinationNumber("4499898400")
                .duration(100L)
                .description("Local call")
                .type(ItemType.CALL)
                .value(BigDecimal.valueOf(0.3))
                .build());

        items.add(BillItem.builder()
                .dataTime(LocalDateTime.now())
                .originNumber("4499898484")
                .destinationNumber("4499889400")
                .duration(250L)
                .description("Another carrier call")
                .type(ItemType.CALL)
                .value(BigDecimal.valueOf(1))
                .build());

        final Bill bill = Bill.builder()
                .carrier(carrier)
                .customer(customer)
                .identifier("123")
                .yearMonth(YearMonth.now())
                .items(items)
                .total(BigDecimal.valueOf(1.3))
                .build();

        final Bill savedBill = billService.save(bill);
        Assert.assertNotNull(savedBill.getId());
        Assert.assertEquals(YearMonth.now(), savedBill.getYearMonth());
        Assert.assertEquals(BigDecimal.valueOf(1.3), savedBill.getTotal());

        Assert.assertEquals(2, savedBill.getItems().size());
        Assert.assertThat(savedBill.getItems(), contains(
                allOf(
                    hasProperty("bill", is(bill)),
                    hasProperty("description", is("Local call"))),
                allOf(
                    hasProperty("bill", is(bill)),
                    hasProperty("description", is("Another carrier call")))
                ));
    }

    @Test
    public void updatingABillWithOneMoreCall() {
        final List<BillItem> items = new ArrayList<>();

        items.add(BillItem.builder()
                .dataTime(LocalDateTime.now())
                .originNumber("4499898639")
                .destinationNumber("4499898400")
                .duration(40L)
                .description("Local call")
                .type(ItemType.CALL)
                .value(BigDecimal.valueOf(0.63))
                .build());

        items.add(BillItem.builder()
                .dataTime(LocalDateTime.now())
                .originNumber("4499898484")
                .destinationNumber("4499999900")
                .duration(40L)
                .description("Another carrier call")
                .type(ItemType.CALL)
                .value(BigDecimal.valueOf(1.45))
                .build());

        final Bill bill = Bill.builder()
                .carrier(carrier)
                .customer(customer)
                .identifier("123")
                .yearMonth(YearMonth.now())
                .items(items)
                .total(BigDecimal.valueOf(1.3))
                .build();

        final Bill savedBill = billService.save(bill);
        Assert.assertEquals(2, savedBill.getItems().size());

        savedBill.getItems().add(BillItem.builder()
                .dataTime(LocalDateTime.now())
                .originNumber("4499898484")
                .destinationNumber("4499999900")
                .duration(100L)
                .description("Roaming Call")
                .type(ItemType.CALL)
                .value(BigDecimal.valueOf(3.98))
                .build());

        final Bill updatedBill = billService.save(savedBill);
        Assert.assertEquals(3, updatedBill.getItems().size());
        Assert.assertThat(updatedBill.getItems(), contains(
                allOf(
                        hasProperty("bill", is(bill)),
                        hasProperty("description", is("Local call"))),
                allOf(
                        hasProperty("bill", is(bill)),
                        hasProperty("description", is("Another carrier call"))),
                allOf(
                        hasProperty("bill", is(bill)),
                        hasProperty("description", is("Roaming Call")))
        ));
    }

}