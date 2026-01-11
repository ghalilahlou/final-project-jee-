import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ChatbotRequest, ChatbotResponse } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class ChatbotService {
    private apiUrl = `${environment.apiUrl}/api/chatbot`;

    constructor(private http: HttpClient) { }

    /**
     * Envoie un message au chatbot
     */
    sendMessage(request: ChatbotRequest): Observable<ChatbotResponse> {
        return this.http.post<ChatbotResponse>(`${this.apiUrl}/chat`, request);
    }

    /**
     * Récupère l'historique des conversations
     */
    getConversationHistory(userId: string): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/history/${userId}`);
    }

    /**
     * Génère un ID de conversation unique
     */
    generateConversationId(): string {
        return `conv_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
    }
}
