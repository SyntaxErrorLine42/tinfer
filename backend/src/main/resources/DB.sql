CREATE SCHEMA "auth";

CREATE TYPE "app_mode" AS ENUM (
  'friends',
  'projects',
  'dating'
);

CREATE TYPE "friendship_status" AS ENUM (
  'pending',
  'accepted',
  'rejected',
  'blocked'
);

CREATE TYPE "project_type" AS ENUM (
  'academic',
  'personal',
  'startup',
  'freelance',
  'internship',
  'job'
);

CREATE TYPE "commitment_level" AS ENUM (
  'low',
  'medium',
  'high'
);

CREATE TYPE "application_status" AS ENUM (
  'pending',
  'accepted',
  'rejected',
  'withdrawn'
);

CREATE TYPE "report_status" AS ENUM (
  'pending',
  'reviewed',
  'action_taken',
  'dismissed'
);

CREATE TYPE "swipe_action" AS ENUM (
  'like',
  'pass',
  'super_like'
);

CREATE TYPE "match_type" AS ENUM (
  'friends',
  'projects',
  'dating'
);

CREATE TYPE "report_reason" AS ENUM (
  'inappropriate_content',
  'harassment',
  'fake_profile',
  'spam',
  'other'
);

CREATE TABLE "profiles" (
                            "id" uuid PRIMARY KEY,
                            "email" varchar(255) UNIQUE NOT NULL,
                            "first_name" varchar(100) NOT NULL,
                            "last_name" varchar(100) NOT NULL,
                            "display_name" varchar(100),
                            "bio" text,
                            "year_of_study" integer,
                            "student_id" varchar(50) UNIQUE,
                            "is_verified" boolean DEFAULT false,
                            "is_active" boolean DEFAULT true,
                            "created_at" timestamp DEFAULT (now()),
                            "updated_at" timestamp DEFAULT (now())
);

CREATE TABLE "departments" (
                               "id" serial PRIMARY KEY,
                               "name" varchar(100) NOT NULL,
                               "code" varchar(20) UNIQUE NOT NULL,
                               "description" text
);

CREATE TABLE "user_departments" (
                                    "user_id" uuid,
                                    "department_id" integer,
                                    "is_primary" boolean DEFAULT true,
                                    PRIMARY KEY ("user_id", "department_id")
);

CREATE TABLE "user_modes" (
                              "id" serial PRIMARY KEY,
                              "user_id" uuid,
                              "mode" app_mode NOT NULL,
                              "is_active" boolean DEFAULT true,
                              "joined_at" timestamp DEFAULT (now()),
                              "paused_at" timestamp
);

CREATE TABLE "mode_preferences" (
                                    "id" serial PRIMARY KEY,
                                    "user_id" uuid,
                                    "mode" app_mode NOT NULL,
                                    "preferences" jsonb,
                                    "updated_at" timestamp DEFAULT (now())
);

CREATE TABLE "photos" (
                          "id" serial PRIMARY KEY,
                          "user_id" uuid,
                          "url" varchar(500) NOT NULL,
                          "display_order" integer DEFAULT 0,
                          "is_primary" boolean DEFAULT false,
                          "mode" app_mode,
                          "uploaded_at" timestamp DEFAULT (now())
);

CREATE TABLE "skills" (
                          "id" serial PRIMARY KEY,
                          "name" varchar(100) UNIQUE NOT NULL,
                          "category" varchar(50),
                          "created_at" timestamp DEFAULT (now())
);

CREATE TABLE "user_skills" (
                               "id" serial PRIMARY KEY,
                               "user_id" uuid,
                               "skill_id" integer,
                               "proficiency_level" integer,
                               "years_of_experience" decimal(3,1)
);

CREATE TABLE "interests" (
                             "id" serial PRIMARY KEY,
                             "name" varchar(100) UNIQUE NOT NULL,
                             "category" varchar(50),
                             "created_at" timestamp DEFAULT (now())
);

CREATE TABLE "user_interests" (
                                  "user_id" uuid,
                                  "interest_id" integer,
                                  PRIMARY KEY ("user_id", "interest_id")
);

CREATE TABLE "courses" (
                           "id" serial PRIMARY KEY,
                           "code" varchar(20) UNIQUE NOT NULL,
                           "name" varchar(200) NOT NULL,
                           "department_id" integer,
                           "semester" integer,
                           "ects" integer
);

CREATE TABLE "user_courses" (
                                "id" serial PRIMARY KEY,
                                "user_id" uuid,
                                "course_id" integer,
                                "semester_taken" varchar(20),
                                "is_current" boolean DEFAULT false,
                                "grade" decimal(3,2)
);

CREATE TABLE "friendships" (
                               "id" serial PRIMARY KEY,
                               "requester_id" uuid,
                               "receiver_id" uuid,
                               "status" friendship_status DEFAULT 'pending',
                               "requested_at" timestamp DEFAULT (now()),
                               "responded_at" timestamp
);

CREATE TABLE "project_posts" (
                                 "id" serial PRIMARY KEY,
                                 "posted_by" uuid,
                                 "title" varchar(200) NOT NULL,
                                 "description" text NOT NULL,
                                 "project_type" project_type NOT NULL,
                                 "commitment_level" commitment_level,
                                 "team_size" integer,
                                 "deadline" date,
                                 "is_paid" boolean DEFAULT false,
                                 "compensation_details" text,
                                 "is_active" boolean DEFAULT true,
                                 "created_at" timestamp DEFAULT (now()),
                                 "updated_at" timestamp DEFAULT (now()),
                                 "expires_at" timestamp
);

CREATE TABLE "project_required_skills" (
                                           "project_id" integer,
                                           "skill_id" integer,
                                           PRIMARY KEY ("project_id", "skill_id")
);

CREATE TABLE "project_preferred_skills" (
                                            "project_id" integer,
                                            "skill_id" integer,
                                            PRIMARY KEY ("project_id", "skill_id")
);

CREATE TABLE "project_applications" (
                                        "id" serial PRIMARY KEY,
                                        "project_id" integer,
                                        "applicant_id" uuid,
                                        "message" text,
                                        "portfolio_url" varchar(500),
                                        "github_url" varchar(500),
                                        "status" application_status DEFAULT 'pending',
                                        "applied_at" timestamp DEFAULT (now()),
                                        "responded_at" timestamp
);

CREATE TABLE "project_profiles" (
                                    "id" serial PRIMARY KEY,
                                    "user_id" uuid,
                                    "title" varchar(200),
                                    "description" text,
                                    "portfolio_url" varchar(500),
                                    "github_url" varchar(500),
                                    "linkedin_url" varchar(500),
                                    "available_from" date,
                                    "hours_per_week" integer,
                                    "is_looking" boolean DEFAULT true,
                                    "updated_at" timestamp DEFAULT (now())
);

CREATE TABLE "dating_swipes" (
                                 "id" serial PRIMARY KEY,
                                 "swiper_id" uuid,
                                 "swiped_id" uuid,
                                 "action" swipe_action NOT NULL,
                                 "swiped_at" timestamp DEFAULT (now())
);

CREATE TABLE "dating_profiles" (
                                   "user_id" uuid PRIMARY KEY,
                                   "looking_for" varchar(50),
                                   "gender" varchar(50),
                                   "show_gender" boolean DEFAULT true,
                                   "prompt_1_question" varchar(200),
                                   "prompt_1_answer" text,
                                   "prompt_2_question" varchar(200),
                                   "prompt_2_answer" text,
                                   "prompt_3_question" varchar(200),
                                   "prompt_3_answer" text,
                                   "updated_at" timestamp DEFAULT (now())
);

CREATE TABLE "matches" (
                           "id" serial PRIMARY KEY,
                           "user1_id" uuid,
                           "user2_id" uuid,
                           "match_type" match_type NOT NULL,
                           "matched_at" timestamp DEFAULT (now()),
                           "is_active" boolean DEFAULT true,
                           "project_id" integer
);

CREATE TABLE "conversations" (
                                 "id" serial PRIMARY KEY,
                                 "match_id" integer,
                                 "conversation_type" match_type NOT NULL,
                                 "created_at" timestamp DEFAULT (now()),
                                 "last_message_at" timestamp,
                                 "is_archived" boolean DEFAULT false
);

CREATE TABLE "conversation_participants" (
                                             "conversation_id" integer,
                                             "user_id" uuid,
                                             "joined_at" timestamp DEFAULT (now()),
                                             "last_read_at" timestamp,
                                             "is_muted" boolean DEFAULT false,
                                             PRIMARY KEY ("conversation_id", "user_id")
);

CREATE TABLE "messages" (
                            "id" serial PRIMARY KEY,
                            "conversation_id" integer,
                            "sender_id" uuid,
                            "content" text NOT NULL,
                            "sent_at" timestamp DEFAULT (now()),
                            "is_read" boolean DEFAULT false,
                            "read_at" timestamp,
                            "attachment_url" varchar(500)
);

CREATE TABLE "user_activity_log" (
                                     "id" serial PRIMARY KEY,
                                     "user_id" uuid,
                                     "activity_type" varchar(50),
                                     "mode" app_mode,
                                     "metadata" jsonb,
                                     "created_at" timestamp DEFAULT (now())
);

CREATE TABLE "profile_views" (
                                 "id" serial PRIMARY KEY,
                                 "viewer_id" uuid,
                                 "viewed_id" uuid,
                                 "mode" app_mode NOT NULL,
                                 "viewed_at" timestamp DEFAULT (now())
);

CREATE TABLE "reports" (
                           "id" serial PRIMARY KEY,
                           "reporter_id" uuid,
                           "reported_id" uuid,
                           "reason" report_reason NOT NULL,
                           "description" text,
                           "mode" app_mode NOT NULL,
                           "status" report_status DEFAULT 'pending',
                           "reported_at" timestamp DEFAULT (now()),
                           "resolved_at" timestamp
);

CREATE TABLE "auth"."users" (
                                "id" uuid PRIMARY KEY,
                                "email" varchar(255)
);

CREATE INDEX ON "profiles" ("email");

CREATE INDEX ON "profiles" ("student_id");

CREATE UNIQUE INDEX ON "user_modes" ("user_id", "mode");

CREATE INDEX ON "user_modes" ("user_id");

CREATE UNIQUE INDEX ON "mode_preferences" ("user_id", "mode");

CREATE INDEX ON "photos" ("user_id");

CREATE INDEX ON "photos" ("mode");

CREATE UNIQUE INDEX ON "photos" ("user_id", "is_primary");

CREATE UNIQUE INDEX ON "user_skills" ("user_id", "skill_id");

CREATE INDEX ON "user_skills" ("user_id");

CREATE INDEX ON "user_skills" ("skill_id");

CREATE INDEX ON "courses" ("code");

CREATE INDEX ON "courses" ("department_id");

CREATE UNIQUE INDEX ON "user_courses" ("user_id", "course_id", "semester_taken");

CREATE INDEX ON "user_courses" ("user_id");

CREATE INDEX "idx_user_courses_current" ON "user_courses" ("user_id", "is_current");

CREATE UNIQUE INDEX ON "friendships" ("requester_id", "receiver_id");

CREATE INDEX ON "friendships" ("requester_id");

CREATE INDEX ON "friendships" ("receiver_id");

CREATE INDEX ON "friendships" ("status");

CREATE INDEX ON "project_posts" ("posted_by");

CREATE INDEX "idx_project_posts_active" ON "project_posts" ("is_active");

CREATE INDEX ON "project_posts" ("created_at");

CREATE INDEX ON "project_required_skills" ("skill_id");

CREATE INDEX ON "project_preferred_skills" ("skill_id");

CREATE UNIQUE INDEX ON "project_applications" ("project_id", "applicant_id");

CREATE INDEX ON "project_applications" ("project_id");

CREATE INDEX ON "project_applications" ("applicant_id");

CREATE INDEX ON "project_applications" ("status");

CREATE UNIQUE INDEX ON "project_profiles" ("user_id");

CREATE INDEX ON "project_profiles" ("is_looking");

CREATE UNIQUE INDEX ON "dating_swipes" ("swiper_id", "swiped_id");

CREATE INDEX ON "dating_swipes" ("swiper_id");

CREATE INDEX ON "dating_swipes" ("swiped_id");

CREATE UNIQUE INDEX ON "matches" ("user1_id", "user2_id", "match_type");

CREATE INDEX ON "matches" ("user1_id");

CREATE INDEX ON "matches" ("user2_id");

CREATE INDEX ON "matches" ("match_type");

CREATE INDEX ON "conversations" ("match_id");

CREATE INDEX ON "conversations" ("last_message_at");

CREATE INDEX ON "conversation_participants" ("user_id");

CREATE INDEX "idx_messages_conversation" ON "messages" ("conversation_id", "sent_at");

CREATE INDEX ON "messages" ("sender_id");

CREATE INDEX "idx_user_activity_user" ON "user_activity_log" ("user_id", "created_at");

CREATE INDEX ON "user_activity_log" ("activity_type");

CREATE INDEX ON "profile_views" ("viewed_id", "mode");

CREATE INDEX ON "profile_views" ("viewer_id");

CREATE INDEX "idx_reports_status" ON "reports" ("status");

CREATE INDEX ON "reports" ("reported_id");

CREATE INDEX ON "reports" ("reporter_id");

COMMENT ON COLUMN "profiles"."id" IS 'Referencira Supabase auth.users';

COMMENT ON COLUMN "profiles"."email" IS '@fer.hr verificiran mail';

COMMENT ON COLUMN "profiles"."year_of_study" IS 'CHECK: 1-5';

COMMENT ON COLUMN "departments"."code" IS 'e.g., CS, EE, ACS';

COMMENT ON COLUMN "user_departments"."user_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "user_departments"."department_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "user_modes"."user_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "mode_preferences"."user_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "mode_preferences"."preferences" IS 'Mode-specific settings';

COMMENT ON COLUMN "photos"."user_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "photos"."url" IS 'Supabase Storage URL';

COMMENT ON COLUMN "photos"."mode" IS 'NULL = all modes';

COMMENT ON COLUMN "skills"."category" IS 'e.g., Frontend, Backend, ML, Hardware';

COMMENT ON COLUMN "user_skills"."user_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "user_skills"."skill_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "user_skills"."proficiency_level" IS 'CHECK: 1-5';

COMMENT ON COLUMN "interests"."category" IS 'e.g., Sports, Music, Gaming, Academic';

COMMENT ON COLUMN "user_interests"."user_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "user_interests"."interest_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "courses"."code" IS 'e.g., RUAP, OOP';

COMMENT ON COLUMN "courses"."semester" IS 'CHECK: 1-10';

COMMENT ON COLUMN "user_courses"."user_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "user_courses"."course_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "user_courses"."semester_taken" IS 'e.g., 2024/2025-Winter';

COMMENT ON COLUMN "user_courses"."grade" IS '2.0 - 5.0 Croatian grading';

COMMENT ON TABLE "friendships" IS 'CHECK: requester_id != receiver_id';

COMMENT ON COLUMN "friendships"."requester_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "friendships"."receiver_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "project_posts"."posted_by" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "project_required_skills"."project_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "project_required_skills"."skill_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "project_preferred_skills"."project_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "project_preferred_skills"."skill_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "project_applications"."project_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "project_applications"."applicant_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "project_profiles"."user_id" IS 'ON DELETE CASCADE, UNIQUE';

COMMENT ON COLUMN "project_profiles"."title" IS 'e.g., Full-stack developer seeking projects';

COMMENT ON TABLE "dating_swipes" IS 'CHECK: swiper_id != swiped_id';

COMMENT ON COLUMN "dating_swipes"."swiper_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "dating_swipes"."swiped_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "dating_profiles"."user_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "dating_profiles"."looking_for" IS 'friendship, relationship, casual, not_sure';

COMMENT ON TABLE "matches" IS 'CHECK: user1_id < user2_id for consistent ordering';

COMMENT ON COLUMN "matches"."user1_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "matches"."user2_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "matches"."project_id" IS 'ON DELETE SET NULL';

COMMENT ON COLUMN "conversations"."match_id" IS 'ON DELETE CASCADE';

COMMENT ON TABLE "conversation_participants" IS 'Tracks per-user conversation state. Although participants can be derived from matches, this table stores individual last_read_at and mute preferences.';

COMMENT ON COLUMN "conversation_participants"."conversation_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "conversation_participants"."user_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "messages"."conversation_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "messages"."sender_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "user_activity_log"."user_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "user_activity_log"."activity_type" IS 'login, swipe, message_sent, profile_view';

COMMENT ON COLUMN "user_activity_log"."metadata" IS 'Additional context';

COMMENT ON TABLE "profile_views" IS 'CHECK: viewer_id != viewed_id';

COMMENT ON COLUMN "profile_views"."viewer_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "profile_views"."viewed_id" IS 'ON DELETE CASCADE';

COMMENT ON TABLE "reports" IS 'CHECK: reporter_id != reported_id';

COMMENT ON COLUMN "reports"."reporter_id" IS 'ON DELETE CASCADE';

COMMENT ON COLUMN "reports"."reported_id" IS 'ON DELETE CASCADE';

COMMENT ON TABLE "auth"."users" IS 'This table is managed by Supabase. Only referenced here for completeness.';

COMMENT ON COLUMN "auth"."users"."id" IS 'Managed by Supabase Auth';

ALTER TABLE "profiles" ADD FOREIGN KEY ("id") REFERENCES "auth"."users" ("id");

ALTER TABLE "user_departments" ADD FOREIGN KEY ("user_id") REFERENCES "profiles" ("id");

ALTER TABLE "user_departments" ADD FOREIGN KEY ("department_id") REFERENCES "departments" ("id");

ALTER TABLE "user_modes" ADD FOREIGN KEY ("user_id") REFERENCES "profiles" ("id");

ALTER TABLE "mode_preferences" ADD FOREIGN KEY ("user_id") REFERENCES "profiles" ("id");

ALTER TABLE "photos" ADD FOREIGN KEY ("user_id") REFERENCES "profiles" ("id");

ALTER TABLE "user_skills" ADD FOREIGN KEY ("user_id") REFERENCES "profiles" ("id");

ALTER TABLE "user_skills" ADD FOREIGN KEY ("skill_id") REFERENCES "skills" ("id");

ALTER TABLE "user_interests" ADD FOREIGN KEY ("user_id") REFERENCES "profiles" ("id");

ALTER TABLE "user_interests" ADD FOREIGN KEY ("interest_id") REFERENCES "interests" ("id");

ALTER TABLE "courses" ADD FOREIGN KEY ("department_id") REFERENCES "departments" ("id");

ALTER TABLE "user_courses" ADD FOREIGN KEY ("user_id") REFERENCES "profiles" ("id");

ALTER TABLE "user_courses" ADD FOREIGN KEY ("course_id") REFERENCES "courses" ("id");

ALTER TABLE "friendships" ADD FOREIGN KEY ("requester_id") REFERENCES "profiles" ("id");

ALTER TABLE "friendships" ADD FOREIGN KEY ("receiver_id") REFERENCES "profiles" ("id");

ALTER TABLE "project_posts" ADD FOREIGN KEY ("posted_by") REFERENCES "profiles" ("id");

ALTER TABLE "project_required_skills" ADD FOREIGN KEY ("project_id") REFERENCES "project_posts" ("id");

ALTER TABLE "project_required_skills" ADD FOREIGN KEY ("skill_id") REFERENCES "skills" ("id");

ALTER TABLE "project_preferred_skills" ADD FOREIGN KEY ("project_id") REFERENCES "project_posts" ("id");

ALTER TABLE "project_preferred_skills" ADD FOREIGN KEY ("skill_id") REFERENCES "skills" ("id");

ALTER TABLE "project_applications" ADD FOREIGN KEY ("project_id") REFERENCES "project_posts" ("id");

ALTER TABLE "project_applications" ADD FOREIGN KEY ("applicant_id") REFERENCES "profiles" ("id");

ALTER TABLE "project_profiles" ADD FOREIGN KEY ("user_id") REFERENCES "profiles" ("id");

ALTER TABLE "dating_swipes" ADD FOREIGN KEY ("swiper_id") REFERENCES "profiles" ("id");

ALTER TABLE "dating_swipes" ADD FOREIGN KEY ("swiped_id") REFERENCES "profiles" ("id");

ALTER TABLE "dating_profiles" ADD FOREIGN KEY ("user_id") REFERENCES "profiles" ("id");

ALTER TABLE "matches" ADD FOREIGN KEY ("user1_id") REFERENCES "profiles" ("id");

ALTER TABLE "matches" ADD FOREIGN KEY ("user2_id") REFERENCES "profiles" ("id");

ALTER TABLE "matches" ADD FOREIGN KEY ("project_id") REFERENCES "project_posts" ("id");

ALTER TABLE "conversations" ADD FOREIGN KEY ("match_id") REFERENCES "matches" ("id");

ALTER TABLE "conversation_participants" ADD FOREIGN KEY ("conversation_id") REFERENCES "conversations" ("id");

ALTER TABLE "conversation_participants" ADD FOREIGN KEY ("user_id") REFERENCES "profiles" ("id");

ALTER TABLE "messages" ADD FOREIGN KEY ("conversation_id") REFERENCES "conversations" ("id");

ALTER TABLE "messages" ADD FOREIGN KEY ("sender_id") REFERENCES "profiles" ("id");

ALTER TABLE "user_activity_log" ADD FOREIGN KEY ("user_id") REFERENCES "profiles" ("id");

ALTER TABLE "profile_views" ADD FOREIGN KEY ("viewer_id") REFERENCES "profiles" ("id");

ALTER TABLE "profile_views" ADD FOREIGN KEY ("viewed_id") REFERENCES "profiles" ("id");

ALTER TABLE "reports" ADD FOREIGN KEY ("reporter_id") REFERENCES "profiles" ("id");

ALTER TABLE "reports" ADD FOREIGN KEY ("reported_id") REFERENCES "profiles" ("id");
