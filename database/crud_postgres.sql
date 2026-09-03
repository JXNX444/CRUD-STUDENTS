--
-- PostgreSQL database dump
--

\restrict kh4H1RbSjXbwbghmK1gXlz2PN143jOXcdzeAfleHdBqgOavJLZrb3cq3AgOQqeG

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: academico; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA academico;


ALTER SCHEMA academico OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: carrera; Type: TABLE; Schema: academico; Owner: postgres
--

CREATE TABLE academico.carrera (
    carrera_id integer NOT NULL,
    codigo character varying(10) NOT NULL,
    nombre character varying(150) NOT NULL,
    facultad character varying(100),
    anios_duracion integer DEFAULT 5 NOT NULL,
    state character varying(9) DEFAULT 'ACTIVO'::character varying NOT NULL,
    row_version integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by integer,
    updated_at timestamp with time zone,
    updated_by integer
);


ALTER TABLE academico.carrera OWNER TO postgres;

--
-- Name: carrera_carrera_id_seq; Type: SEQUENCE; Schema: academico; Owner: postgres
--

CREATE SEQUENCE academico.carrera_carrera_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE academico.carrera_carrera_id_seq OWNER TO postgres;

--
-- Name: carrera_carrera_id_seq; Type: SEQUENCE OWNED BY; Schema: academico; Owner: postgres
--

ALTER SEQUENCE academico.carrera_carrera_id_seq OWNED BY academico.carrera.carrera_id;


--
-- Name: curso; Type: TABLE; Schema: academico; Owner: postgres
--

CREATE TABLE academico.curso (
    curso_id integer NOT NULL,
    codigo character varying(20) NOT NULL,
    nombre character varying(120) NOT NULL,
    carrera_id integer NOT NULL,
    state character varying(9) DEFAULT 'ACTIVO'::character varying NOT NULL,
    row_version integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by integer,
    updated_at timestamp with time zone,
    updated_by integer,
    CONSTRAINT ck_curso_state CHECK (((state)::text = ANY ((ARRAY['ACTIVO'::character varying, 'INACTIVO'::character varying, 'ELIMINADO'::character varying])::text[])))
);


ALTER TABLE academico.curso OWNER TO postgres;

--
-- Name: curso_curso_id_seq; Type: SEQUENCE; Schema: academico; Owner: postgres
--

ALTER TABLE academico.curso ALTER COLUMN curso_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME academico.curso_curso_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: estudiante; Type: TABLE; Schema: academico; Owner: postgres
--

CREATE TABLE academico.estudiante (
    estudiante_id integer NOT NULL,
    carnet character varying(20) NOT NULL,
    nombre_completo character varying(100) NOT NULL,
    correo public.citext NOT NULL,
    telefono character varying(8),
    carrera character varying(100) NOT NULL,
    state character varying(9) DEFAULT 'ACTIVO'::character varying NOT NULL,
    row_version integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by integer,
    updated_at timestamp with time zone,
    updated_by integer,
    CONSTRAINT ck_estudiante_carnet CHECK (((carnet)::text ~ '^[0-9]{6,20}$'::text)),
    CONSTRAINT ck_estudiante_state CHECK (((state)::text = ANY ((ARRAY['ACTIVO'::character varying, 'INACTIVO'::character varying, 'ELIMINADO'::character varying])::text[]))),
    CONSTRAINT ck_estudiante_telefono CHECK (((telefono IS NULL) OR ((telefono)::text ~ '^[0-9]{8}$'::text)))
);


ALTER TABLE academico.estudiante OWNER TO postgres;

--
-- Name: TABLE estudiante; Type: COMMENT; Schema: academico; Owner: postgres
--

COMMENT ON TABLE academico.estudiante IS 'Hoja de trabajo 1: CRUD de estudiantes con baja logica';


--
-- Name: estudiante_estudiante_id_seq; Type: SEQUENCE; Schema: academico; Owner: postgres
--

ALTER TABLE academico.estudiante ALTER COLUMN estudiante_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME academico.estudiante_estudiante_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: inscripcion; Type: TABLE; Schema: academico; Owner: postgres
--

CREATE TABLE academico.inscripcion (
    inscripcion_id integer NOT NULL,
    estudiante_id integer NOT NULL,
    curso_id integer NOT NULL,
    fecha_inscripcion timestamp with time zone DEFAULT now() NOT NULL,
    state character varying(9) DEFAULT 'ACTIVO'::character varying NOT NULL,
    row_version integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by integer,
    updated_at timestamp with time zone,
    updated_by integer,
    CONSTRAINT ck_inscripcion_state CHECK (((state)::text = ANY ((ARRAY['ACTIVO'::character varying, 'INACTIVO'::character varying, 'ELIMINADO'::character varying])::text[])))
);


ALTER TABLE academico.inscripcion OWNER TO postgres;

--
-- Name: inscripcion_inscripcion_id_seq; Type: SEQUENCE; Schema: academico; Owner: postgres
--

ALTER TABLE academico.inscripcion ALTER COLUMN inscripcion_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME academico.inscripcion_inscripcion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: v_asignaciones; Type: VIEW; Schema: academico; Owner: postgres
--

CREATE VIEW academico.v_asignaciones AS
 SELECT e.nombre_completo AS estudiante,
    ca.nombre AS carrera,
    cu.codigo,
    cu.nombre AS curso,
    i.fecha_inscripcion
   FROM (((academico.inscripcion i
     JOIN academico.estudiante e ON ((e.estudiante_id = i.estudiante_id)))
     JOIN academico.curso cu ON ((cu.curso_id = i.curso_id)))
     JOIN academico.carrera ca ON ((ca.carrera_id = cu.carrera_id)))
  WHERE ((i.state)::text <> 'ELIMINADO'::text);


ALTER VIEW academico.v_asignaciones OWNER TO postgres;

--
-- Name: carrera carrera_id; Type: DEFAULT; Schema: academico; Owner: postgres
--

ALTER TABLE ONLY academico.carrera ALTER COLUMN carrera_id SET DEFAULT nextval('academico.carrera_carrera_id_seq'::regclass);


--
-- Data for Name: carrera; Type: TABLE DATA; Schema: academico; Owner: postgres
--

COPY academico.carrera (carrera_id, codigo, nombre, facultad, anios_duracion, state, row_version, created_at, created_by, updated_at, updated_by) FROM stdin;
4	ELN	Ingenieria Electronica	Ingenieria	5	ELIMINADO	0	2026-08-08 13:41:06.060115-07	\N	\N	\N
5	AMB	Ingenieria en Gestion Ambiental	Ingenieria	5	ELIMINADO	0	2026-08-08 13:41:06.060115-07	\N	\N	\N
6	ADM	Licenciatura en Administracion de Empresas	Ciencias Economicas	5	ELIMINADO	0	2026-08-08 13:41:06.060115-07	\N	\N	\N
7	CPA	Licenciatura en Contaduria Publica y Auditoria	Ciencias Economicas	5	ELIMINADO	0	2026-08-08 13:41:06.060115-07	\N	\N	\N
9	PSI	Licenciatura en Psicologia	Humanidades	5	ELIMINADO	0	2026-08-08 13:41:06.060115-07	\N	\N	\N
10	MED	Medico y Cirujano	Ciencias Medicas	6	ELIMINADO	0	2026-08-08 13:41:06.060115-07	\N	\N	\N
11	COM	Licenciatura en Ciencias de la Comunicacion	Humanidades	5	ELIMINADO	0	2026-08-08 13:41:06.060115-07	\N	\N	\N
12	ARQ	Arquitectura	Arquitectura	5	ELIMINADO	0	2026-08-08 13:41:06.060115-07	\N	\N	\N
13	MEC	Ingenieria en Mecatronica	Ingenieria	5	ELIMINADO	0	2026-08-08 14:16:45.902364-07	\N	\N	\N
14	psi	Psicologia d ela ia	psicologia	5	ELIMINADO	0	2026-08-08 14:22:56.997991-07	\N	\N	\N
15	AN	Analisis de la ia	Analisis	5	ELIMINADO	0	2026-08-08 14:23:49.908522-07	\N	\N	\N
16	NUT	Licenciatura en Nutricion	Ciencias Medicas	5	ELIMINADO	0	2026-08-08 14:29:10.881329-07	\N	\N	\N
1	SIS	Ingenieria en Sistemas de Informacion y Ciencias de la Computacion	Ingenieria	5	ACTIVO	0	2026-08-08 13:41:06.060115-07	\N	\N	\N
2	IND	Ingenieria Industrial	Ingenieria	5	ACTIVO	0	2026-08-08 13:41:06.060115-07	\N	\N	\N
8	DER	Licenciatura en Ciencias Juridicas y Sociales	Ciencias Juridicas	5	ACTIVO	0	2026-08-08 13:41:06.060115-07	\N	\N	\N
3	CIV	Ingenieria Civil	Ingenieria	5	ELIMINADO	0	2026-08-08 13:41:06.060115-07	\N	\N	\N
\.


--
-- Data for Name: curso; Type: TABLE DATA; Schema: academico; Owner: postgres
--

COPY academico.curso (curso_id, codigo, nombre, carrera_id, state, row_version, created_at, created_by, updated_at, updated_by) FROM stdin;
1	CIV-103	Diseno Estructural	3	ACTIVO	0	2026-08-22 13:33:49.221659-07	\N	\N	\N
2	CIV-102	Mecanica de Suelos	3	ACTIVO	0	2026-08-22 13:33:49.221659-07	\N	\N	\N
3	CIV-101	Estatica	3	ACTIVO	0	2026-08-22 13:33:49.221659-07	\N	\N	\N
4	IND-103	Procesos Industriales	2	ACTIVO	0	2026-08-22 14:02:02.352282-07	\N	\N	\N
5	DER-103	Derecho Constitucional	8	ACTIVO	0	2026-08-22 14:02:02.352282-07	\N	\N	\N
6	SIS-102	Bases de Datos	1	ACTIVO	0	2026-08-22 14:02:02.352282-07	\N	\N	\N
7	IND-101	Investigacion de Operaciones	2	ACTIVO	0	2026-08-22 14:02:02.352282-07	\N	\N	\N
8	DER-102	Derecho Penal	8	ACTIVO	0	2026-08-22 14:02:02.352282-07	\N	\N	\N
9	DER-101	Derecho Civil	8	ACTIVO	0	2026-08-22 14:02:02.352282-07	\N	\N	\N
10	IND-102	Control de Calidad	2	ACTIVO	0	2026-08-22 14:02:02.352282-07	\N	\N	\N
11	SIS-101	Programacion I	1	ACTIVO	0	2026-08-22 14:02:02.352282-07	\N	\N	\N
12	SIS-103	Redes de Computadoras	1	ACTIVO	0	2026-08-22 14:02:02.352282-07	\N	\N	\N
\.


--
-- Data for Name: estudiante; Type: TABLE DATA; Schema: academico; Owner: postgres
--

COPY academico.estudiante (estudiante_id, carnet, nombre_completo, correo, telefono, carrera, state, row_version, created_at, created_by, updated_at, updated_by) FROM stdin;
3	555555644	Juan Perez	juanp@gmail.com	22234567	Inge	ACTIVO	0	2026-08-01 14:18:59.062045-07	\N	\N	\N
4	6365739	Luis Por	lupo@gmail.com	43167895	Ingenieria	ELIMINADO	1	2026-08-01 14:36:08.548933-07	\N	2026-08-01 14:43:56.054194-07	\N
1	2024001	Juan Perez	juan@umg.edu.gt	55551234	Ingenieria en Sistemas	ELIMINADO	1	2026-08-01 14:11:17.268596-07	\N	2026-08-08 11:20:52.011324-07	\N
5	56677743	Jonathan Barrera	jonabarrera1@gmail.com	33175008	Ingenieria en Sistemas de Informacion y Ciencias de la Computacion	ACTIVO	0	2026-08-22 14:04:27.555234-07	\N	\N	\N
7	656577777	Edy	edy.ramirezc@gmail.com	54456789	Ingenieria en Sistemas de Informacion y Ciencias de la Computacion	ACTIVO	0	2026-08-22 14:06:38.52129-07	\N	\N	\N
\.


--
-- Data for Name: inscripcion; Type: TABLE DATA; Schema: academico; Owner: postgres
--

COPY academico.inscripcion (inscripcion_id, estudiante_id, curso_id, fecha_inscripcion, state, row_version, created_at, created_by, updated_at, updated_by) FROM stdin;
1	5	11	2026-08-22 14:04:47.884511-07	ACTIVO	0	2026-08-22 14:04:47.886523-07	\N	\N	\N
2	5	6	2026-08-22 14:04:47.896774-07	ACTIVO	0	2026-08-22 14:04:47.896774-07	\N	\N	\N
3	5	7	2026-08-22 14:04:47.907273-07	ACTIVO	0	2026-08-22 14:04:47.907273-07	\N	\N	\N
4	5	10	2026-08-22 14:04:47.915221-07	ACTIVO	0	2026-08-22 14:04:47.915221-07	\N	\N	\N
5	5	9	2026-08-22 14:04:47.92446-07	ACTIVO	0	2026-08-22 14:04:47.92446-07	\N	\N	\N
6	7	11	2026-08-22 14:07:14.326144-07	ACTIVO	0	2026-08-22 14:07:14.326144-07	\N	\N	\N
7	7	10	2026-08-22 14:07:14.330846-07	ACTIVO	0	2026-08-22 14:07:14.332857-07	\N	\N	\N
8	7	5	2026-08-22 14:07:14.337531-07	ACTIVO	0	2026-08-22 14:07:14.337531-07	\N	\N	\N
9	7	6	2026-08-22 14:07:14.343994-07	ACTIVO	0	2026-08-22 14:07:14.343994-07	\N	\N	\N
\.


--
-- Name: carrera_carrera_id_seq; Type: SEQUENCE SET; Schema: academico; Owner: postgres
--

SELECT pg_catalog.setval('academico.carrera_carrera_id_seq', 16, true);


--
-- Name: curso_curso_id_seq; Type: SEQUENCE SET; Schema: academico; Owner: postgres
--

SELECT pg_catalog.setval('academico.curso_curso_id_seq', 12, true);


--
-- Name: estudiante_estudiante_id_seq; Type: SEQUENCE SET; Schema: academico; Owner: postgres
--

SELECT pg_catalog.setval('academico.estudiante_estudiante_id_seq', 7, true);


--
-- Name: inscripcion_inscripcion_id_seq; Type: SEQUENCE SET; Schema: academico; Owner: postgres
--

SELECT pg_catalog.setval('academico.inscripcion_inscripcion_id_seq', 9, true);


--
-- Name: carrera carrera_pkey; Type: CONSTRAINT; Schema: academico; Owner: postgres
--

ALTER TABLE ONLY academico.carrera
    ADD CONSTRAINT carrera_pkey PRIMARY KEY (carrera_id);


--
-- Name: curso curso_pkey; Type: CONSTRAINT; Schema: academico; Owner: postgres
--

ALTER TABLE ONLY academico.curso
    ADD CONSTRAINT curso_pkey PRIMARY KEY (curso_id);


--
-- Name: estudiante estudiante_pkey; Type: CONSTRAINT; Schema: academico; Owner: postgres
--

ALTER TABLE ONLY academico.estudiante
    ADD CONSTRAINT estudiante_pkey PRIMARY KEY (estudiante_id);


--
-- Name: inscripcion inscripcion_pkey; Type: CONSTRAINT; Schema: academico; Owner: postgres
--

ALTER TABLE ONLY academico.inscripcion
    ADD CONSTRAINT inscripcion_pkey PRIMARY KEY (inscripcion_id);


--
-- Name: carrera uq_carrera_codigo; Type: CONSTRAINT; Schema: academico; Owner: postgres
--

ALTER TABLE ONLY academico.carrera
    ADD CONSTRAINT uq_carrera_codigo UNIQUE (codigo);


--
-- Name: ix_curso_carrera; Type: INDEX; Schema: academico; Owner: postgres
--

CREATE INDEX ix_curso_carrera ON academico.curso USING btree (carrera_id);


--
-- Name: ux_curso_codigo_activo; Type: INDEX; Schema: academico; Owner: postgres
--

CREATE UNIQUE INDEX ux_curso_codigo_activo ON academico.curso USING btree (codigo) WHERE ((state)::text <> 'ELIMINADO'::text);


--
-- Name: ux_estudiante_carnet_activo; Type: INDEX; Schema: academico; Owner: postgres
--

CREATE UNIQUE INDEX ux_estudiante_carnet_activo ON academico.estudiante USING btree (carnet) WHERE ((state)::text <> 'ELIMINADO'::text);


--
-- Name: ux_estudiante_correo_activo; Type: INDEX; Schema: academico; Owner: postgres
--

CREATE UNIQUE INDEX ux_estudiante_correo_activo ON academico.estudiante USING btree (lower((correo)::text)) WHERE ((state)::text <> 'ELIMINADO'::text);


--
-- Name: ux_inscripcion_est_curso_activo; Type: INDEX; Schema: academico; Owner: postgres
--

CREATE UNIQUE INDEX ux_inscripcion_est_curso_activo ON academico.inscripcion USING btree (estudiante_id, curso_id) WHERE ((state)::text <> 'ELIMINADO'::text);


--
-- Name: curso curso_carrera_id_fkey; Type: FK CONSTRAINT; Schema: academico; Owner: postgres
--

ALTER TABLE ONLY academico.curso
    ADD CONSTRAINT curso_carrera_id_fkey FOREIGN KEY (carrera_id) REFERENCES academico.carrera(carrera_id);


--
-- Name: inscripcion inscripcion_curso_id_fkey; Type: FK CONSTRAINT; Schema: academico; Owner: postgres
--

ALTER TABLE ONLY academico.inscripcion
    ADD CONSTRAINT inscripcion_curso_id_fkey FOREIGN KEY (curso_id) REFERENCES academico.curso(curso_id);


--
-- Name: inscripcion inscripcion_estudiante_id_fkey; Type: FK CONSTRAINT; Schema: academico; Owner: postgres
--

ALTER TABLE ONLY academico.inscripcion
    ADD CONSTRAINT inscripcion_estudiante_id_fkey FOREIGN KEY (estudiante_id) REFERENCES academico.estudiante(estudiante_id);


--
-- PostgreSQL database dump complete
--

\unrestrict kh4H1RbSjXbwbghmK1gXlz2PN143jOXcdzeAfleHdBqgOavJLZrb3cq3AgOQqeG

