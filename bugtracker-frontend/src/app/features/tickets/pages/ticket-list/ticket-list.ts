import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TicketService } from '../../tickets.service';

@Component({
  selector: 'app-ticket-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ticket-list.html',
  styleUrl: './ticket-list.css'
})
export class TicketListComponent implements OnInit {
  tickets: any[] = [];
  newTicket: any = { title: '', description: '', status: 'TODO', priority: 'MEDIUM' };

  constructor(private ticketService: TicketService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadTickets();
  }

  loadTickets() {
    this.ticketService.getTickets().subscribe((data: any) => {
      this.tickets = data;
      this.cdr.detectChanges();
    });
  }

  onCreateTicket() {
    this.ticketService.createTicket(this.newTicket).subscribe((created: any) => {
      this.tickets.push(created);
      this.newTicket = { title: '', description: '', status: 'TODO', priority: 'MEDIUM' };
      this.cdr.detectChanges();
    });
  }

  onDeleteTicket(id: number) {
    this.ticketService.deleteTicket(id).subscribe(() => {
      this.tickets = this.tickets.filter(t => t.id !== id);
      this.cdr.detectChanges();
    });
  }

  onUpdateTicket(ticket: any) {
    this.ticketService.updateTicket(ticket.id, ticket).subscribe((updated: any) => {
      const index = this.tickets.findIndex(t => t.id === ticket.id);
      this.tickets[index] = updated;
      this.cdr.detectChanges();
    });
  }
}