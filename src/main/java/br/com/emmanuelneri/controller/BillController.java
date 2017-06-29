package br.com.emmanuelneri.controller;

import br.com.emmanuelneri.model.Bill;
import br.com.emmanuelneri.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Bill> findAll() {
        return billService.findAll();
    }

    @RequestMapping(value = "/paged/{page}/{size}", method = RequestMethod.GET)
    public Page<Bill> findAllPaged(@PathVariable("page") int page, @PathVariable("size") int size) {
        return billService.findPaginable(page, size);
    }

    @RequestMapping(value = "/byUk/{customerId}/{identifier}/{yearMonth}", method = RequestMethod.GET)
    public Bill findByUk(@PathVariable("customerId") Long customerId, @PathVariable("identifier") String identifier, @PathVariable("yearMonth") YearMonth yearMonth) {
        return billService.findByUk(customerId, identifier, yearMonth);
    }

}