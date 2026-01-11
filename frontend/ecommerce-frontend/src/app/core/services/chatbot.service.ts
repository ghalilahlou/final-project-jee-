import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { ApiService } from './api.service';
import { ChatMessage } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class ChatbotService {
    private endpoint = '/api/chatbot';
    private messagesSubject = new Subject<ChatMessage>();
    public messages$ = this.messagesSubject.asObservable();

    constructor(private api: ApiService) { }

    /**
     * Send message to chatbot
     */
    sendMessage(userId: string, message: string): Observable<ChatMessage> {
        const chatMessage: ChatMessage = {
            userId,
            message
        };

        return this.api.post<ChatMessage>(`${this.endpoint}/message`, chatMessage);
    }

    /**
     * Broadcast message to subscribers
     */
    broadcastMessage(message: ChatMessage): void {
        this.messagesSubject.next(message);
    }
}
