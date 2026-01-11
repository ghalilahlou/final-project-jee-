import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ChatbotService } from '../../core/services/chatbot.service';
import { ChatbotRequest, ChatbotResponse } from '../../core/models/api.models';

interface Message {
    text: string;
    isUser: boolean;
    timestamp: Date;
}

@Component({
    selector: 'app-chatbot',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './chatbot.component.html',
    styleUrls: ['./chatbot.component.scss']
})
export class ChatbotComponent implements OnInit {
    messages: Message[] = [
        {
            text: 'Bonjour! 👋 Je suis votre assistant virtuel. Comment puis-je vous aider aujourd\'hui?',
            isUser: false,
            timestamp: new Date()
        }
    ];

    userMessage = '';
    isTyping = false;
    conversationId = '';

    constructor(private chatbotService: ChatbotService) { }

    ngOnInit() {
        // Générer un ID de conversation unique
        this.conversationId = this.chatbotService.generateConversationId();
    }

    sendMessage() {
        if (!this.userMessage.trim()) return;

        // Add user message
        this.messages.push({
            text: this.userMessage,
            isUser: true,
            timestamp: new Date()
        });

        const userMsg = this.userMessage;
        this.userMessage = '';

        // Simulate bot typing
        this.isTyping = true;

        // Créer la requête pour le backend
        const request: ChatbotRequest = {
            conversationId: this.conversationId,
            message: userMsg
        };

        // Appel au service backend
        this.chatbotService.sendMessage(request).subscribe({
            next: (response: ChatbotResponse) => {
                this.isTyping = false;
                this.messages.push({
                    text: response.response,
                    isUser: false,
                    timestamp: new Date(response.timestamp)
                });
                this.scrollToBottom();
            },
            error: (error: any) => {
                console.error('Chatbot error:', error);
                this.isTyping = false;
                // Fallback to local response if backend is unavailable
                this.messages.push({
                    text: this.generateLocalBotResponse(userMsg),
                    isUser: false,
                    timestamp: new Date()
                });
                this.scrollToBottom();
            }
        });

        this.scrollToBottom();
    }

    // Fallback local response generation (used when backend is not available)
    private generateLocalBotResponse(userMsg: string): string {
        const msg = userMsg.toLowerCase();

        if (msg.includes('produit') || msg.includes('article')) {
            return '🛍️ Nous avons une large gamme de produits dans différentes catégories : Électronique, Mode, Maison, Sports, etc. Vous pouvez parcourir notre catalogue en cliquant sur "Produits" dans le menu.';
        } else if (msg.includes('commande') || msg.includes('order')) {
            return '📦 Pour suivre vos commandes, rendez-vous dans la section "Mes Commandes". Vous y trouverez l\'historique complet de vos achats et leur statut de livraison.';
        } else if (msg.includes('livraison') || msg.includes('shipping')) {
            return '🚚 La livraison est gratuite pour toute commande supérieure à 500 MAD. Le délai de livraison standard est de 2-5 jours ouvrables.';
        } else if (msg.includes('paiement') || msg.includes('payment')) {
            return '💳 Nous acceptons plusieurs modes de paiement : cartes bancaires, PayPal, et paiement à la livraison dans certaines zones.';
        } else if (msg.includes('retour') || msg.includes('remboursement')) {
            return '↩️ Vous avez 30 jours pour retourner un article non utilisé. Contactez notre service client pour initier un retour.';
        } else if (msg.includes('prix') || msg.includes('price')) {
            return '💰 Nos prix sont compétitifs et nous proposons régulièrement des promotions. Consultez notre page produits pour voir nos meilleures offres!';
        } else if (msg.includes('aide') || msg.includes('help')) {
            return '🤝 Je peux vous aider avec :\n- Informations sur les produits\n- Suivi de commandes\n- Livraison et retours\n- Modes de paiement\n- Et bien plus encore! N\'hésitez pas à me poser vos questions.';
        } else {
            return '🤖 Je suis là pour vous aider! Vous pouvez me poser des questions sur nos produits, les commandes, la livraison, ou tout autre sujet concernant notre service.';
        }
    }

    scrollToBottom() {
        setTimeout(() => {
            const chatContainer = document.querySelector('.chat-messages');
            if (chatContainer) {
                chatContainer.scrollTop = chatContainer.scrollHeight;
            }
        }, 100);
    }

    clearChat() {
        // Créer une nouvelle conversation
        this.conversationId = this.chatbotService.generateConversationId();
        this.messages = [
            {
                text: 'Bonjour! 👋 Je suis votre assistant virtuel. Comment puis-je vous aider aujourd\'hui?',
                isUser: false,
                timestamp: new Date()
            }
        ];
    }
}
