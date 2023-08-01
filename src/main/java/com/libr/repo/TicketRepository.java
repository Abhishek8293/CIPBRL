package com.libr.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.libr.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, String> {
		


}
