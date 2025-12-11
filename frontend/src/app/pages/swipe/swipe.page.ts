import { Component, signal, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonComponent } from '../../shared/components/button-wrapper/button-wrapper.component';
import { CardComponent } from '../../shared/components/card/card.component';
import { IconComponent } from '../../shared/components/icon-wrapper/icon-wrapper.component';
import { AvatarComponent } from '../../shared/components/avatar/avatar.component';
import { NgStyle } from '@angular/common';

interface Profile {
  id: number;
  name: string;
  age: number;
  bio: string;
  photos: string[];
  interests: string[];
  distance: number;
  currentPhotoIndex: number;
}

@Component({
  selector: 'app-swipe',
  imports: [
    ButtonComponent,
    CardComponent,
    IconComponent,
    AvatarComponent,
    NgStyle,
  ],
  templateUrl: './swipe.page.html',
  styleUrl: './swipe.page.css',
})
export class SwipePage implements AfterViewInit {
  @ViewChild('swipeCard') swipeCard?: ElementRef<HTMLDivElement>;

  profiles = signal<Profile[]>([
    {
      id: 1,
      name: 'Emma',
      age: 24,
      bio: '🎨 Artist | 📸 Photography enthusiast | ☕ Coffee lover | Always up for adventures',
      photos: [
        'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=800',
        'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800',
        'https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=800',
      ],
      interests: ['Art', 'Photography', 'Travel', 'Coffee'],
      distance: 3,
      currentPhotoIndex: 0,
    },
    {
      id: 2,
      name: 'Alex',
      age: 27,
      bio: '🏋️ Fitness coach | 🌮 Foodie | 🎮 Gamer | Living life to the fullest',
      photos: [
        'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=800',
        'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800',
        'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=800',
      ],
      interests: ['Fitness', 'Food', 'Gaming', 'Music'],
      distance: 5,
      currentPhotoIndex: 0,
    },
    {
      id: 3,
      name: 'Sophie',
      age: 23,
      bio: '📚 Book lover | 🎵 Music festival junkie | 🌍 World traveler | Always curious',
      photos: [
        'https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?w=800',
        'https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=800',
        'https://images.unsplash.com/photo-1525134479668-1bee5c7c6845?w=800',
      ],
      interests: ['Books', 'Music', 'Travel', 'Hiking'],
      distance: 2,
      currentPhotoIndex: 0,
    },
    {
      id: 4,
      name: 'Jordan',
      age: 26,
      bio: '🎬 Film buff | 🍕 Pizza connoisseur | 🏄 Surfer | Let\'s grab a slice!',
      photos: [
        'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=800',
        'https://images.unsplash.com/photo-1502823403499-6ccfcf4fb453?w=800',
        'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=800',
      ],
      interests: ['Movies', 'Surfing', 'Food', 'Beach'],
      distance: 7,
      currentPhotoIndex: 0,
    },
    {
      id: 5,
      name: 'Maya',
      age: 25,
      bio: '🧘 Yoga instructor | 🌱 Plant mom | ✈️ Travel addict | Namaste',
      photos: [
        'https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=800',
        'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800',
        'https://images.unsplash.com/photo-1521577352947-9bb58764b69a?w=800',
      ],
      interests: ['Yoga', 'Plants', 'Travel', 'Wellness'],
      distance: 4,
      currentPhotoIndex: 0,
    },
  ]);

  currentIndex = signal(0);
  isDragging = signal(false);
  dragX = signal(0);
  dragY = signal(0);
  rotation = signal(0);
  showMatchModal = signal(false);
  lastMatchedProfile = signal<Profile | null>(null);

  private startX = 0;
  private startY = 0;

  constructor(private router: Router) {}

  ngAfterViewInit() {
    this.setupGestureListeners();
  }

  get currentProfile(): Profile | null {
    return this.profiles()[this.currentIndex()] || null;
  }

  setupGestureListeners() {
    if (!this.swipeCard) return;

    const card = this.swipeCard.nativeElement;
    let isTouching = false;

    // Mouse events
    card.addEventListener('mousedown', (e: MouseEvent) => {
      isTouching = true;
      this.startX = e.clientX;
      this.startY = e.clientY;
      this.isDragging.set(true);
    });

    document.addEventListener('mousemove', (e: MouseEvent) => {
      if (!isTouching) return;
      const deltaX = e.clientX - this.startX;
      const deltaY = e.clientY - this.startY;
      this.dragX.set(deltaX);
      this.dragY.set(deltaY);
      this.rotation.set(deltaX / 20);
    });

    document.addEventListener('mouseup', () => {
      if (!isTouching) return;
      isTouching = false;
      this.handleDragEnd();
    });

    // Touch events
    card.addEventListener('touchstart', (e: TouchEvent) => {
      this.startX = e.touches[0].clientX;
      this.startY = e.touches[0].clientY;
      this.isDragging.set(true);
    });

    card.addEventListener('touchmove', (e: TouchEvent) => {
      const deltaX = e.touches[0].clientX - this.startX;
      const deltaY = e.touches[0].clientY - this.startY;
      this.dragX.set(deltaX);
      this.dragY.set(deltaY);
      this.rotation.set(deltaX / 20);
    });

    card.addEventListener('touchend', () => {
      this.handleDragEnd();
    });
  }

  handleDragEnd() {
    const threshold = 100;
    const dragXValue = this.dragX();

    if (Math.abs(dragXValue) > threshold) {
      if (dragXValue > 0) {
        this.animateSwipe('right');
        this.like();
      } else {
        this.animateSwipe('left');
        this.pass();
      }
    } else {
      this.resetCard();
    }
  }

  animateSwipe(direction: 'left' | 'right') {
    const distance = direction === 'right' ? 1000 : -1000;
    this.dragX.set(distance);
    this.rotation.set(distance / 20);

    setTimeout(() => {
      this.nextProfile();
      this.resetCard();
    }, 300);
  }

  resetCard() {
    this.isDragging.set(false);
    this.dragX.set(0);
    this.dragY.set(0);
    this.rotation.set(0);
  }

  nextProfile() {
    if (this.currentIndex() < this.profiles().length - 1) {
      this.currentIndex.update((i) => i + 1);
    } else {
      // All profiles viewed
      this.currentIndex.set(0);
    }
  }

  like() {
    const profile = this.currentProfile;
    if (profile) {
      console.log('Liked:', profile.name);
      // Simulate match (50% chance)
      if (Math.random() > 0.5) {
        this.lastMatchedProfile.set(profile);
        setTimeout(() => this.showMatchModal.set(true), 400);
      }
    }
  }

  pass() {
    const profile = this.currentProfile;
    if (profile) {
      console.log('Passed:', profile.name);
    }
  }

  superLike() {
    const profile = this.currentProfile;
    if (profile) {
      console.log('Super liked:', profile.name);
      this.lastMatchedProfile.set(profile);
      setTimeout(() => this.showMatchModal.set(true), 400);
      this.animateSwipe('right');
      this.nextProfile();
    }
  }

  rewind() {
    if (this.currentIndex() > 0) {
      this.currentIndex.update((i) => i - 1);
    }
  }

  nextPhoto() {
    const profile = this.currentProfile;
    if (profile && profile.currentPhotoIndex < profile.photos.length - 1) {
      profile.currentPhotoIndex++;
    }
  }

  prevPhoto() {
    const profile = this.currentProfile;
    if (profile && profile.currentPhotoIndex > 0) {
      profile.currentPhotoIndex--;
    }
  }

  closeMatchModal() {
    this.showMatchModal.set(false);
  }

  sendMessage() {
    this.showMatchModal.set(false);
    // Navigate to messages
    console.log('Send message');
  }

  keepSwiping() {
    this.showMatchModal.set(false);
  }
}

