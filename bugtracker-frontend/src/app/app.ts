import { Component } from '@angular/core';
import { TicketListComponent } from './features/tickets/pages/ticket-list/ticket-list';

@Component({
  selector: 'app-root',
  imports: [TicketListComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
}