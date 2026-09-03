import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  apiUrl = 'http://localhost:8080/api/tickets';

  constructor(private http: HttpClient) {}

  getTickets() {
    return this.http.get(this.apiUrl);
  }

  createTicket(ticket: any) {
    return this.http.post(this.apiUrl, ticket);
  }

  deleteTicket(id: number) {
    return this.http.delete(this.apiUrl + '/' + id);
  }

  updateTicket(id: number, ticket: any) {
    return this.http.put(this.apiUrl + '/' + id, ticket);
  }
}